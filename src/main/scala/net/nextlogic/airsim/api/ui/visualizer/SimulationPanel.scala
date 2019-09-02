package net.nextlogic.airsim.api.ui.visualizer

import java.awt.geom.{AffineTransform, Path2D}
import java.awt.{BasicStroke, Color, Dimension, Graphics2D}

import net.nextlogic.airsim.api.simulators.settings.PilotSettings
import net.nextlogic.airsim.api.ui.common.UiUtils
import net.nextlogic.airsim.api.utils.{Quaternionr, Vector3r, VehicleSettings}

import scala.collection.mutable
import scala.swing.Panel

class SimulationPanel() extends Panel {
  var pilotSettings: Seq[PilotSettings] = UiUtils.defaultPlayers

  val colors: Map[String, Color] = pilotSettings
    .foldLeft(Map[String, Color]())( (acc, settings) => acc.updated(settings.name, settings.color.color))

  var paths: mutable.Map[VehicleSettings, Path2D] = mutable.Map[VehicleSettings, Path2D]()
  var orientations = mutable.Map[String, Double]()
  var currentTime: Long = 0L
  val initialScale = 50.0
  val offset = 10

  val pursuerIcon = Icons.fighterJet
  val evaderIcon = Icons.plane


  preferredSize = new Dimension(1200, 800)

  def addSegment(segment: PathSegment): Path2D = {
    // println(s"${segment.vehicleSettings.name}: Adding segment to ${segment.point} ")
    orientations.update(segment.vehicleSettings.name, segment.orientation.yaw)
    val path = paths.getOrElse(segment.vehicleSettings, newPath(segment.point))
    path.lineTo(segment.point.x * initialScale, segment.point.y * initialScale)
    currentTime = segment.time
    paths.update(segment.vehicleSettings, path)
    repaint()
    path
  }

  def newPath(point: Vector3r): Path2D = {
    val path = new Path2D.Double()
    path.moveTo(point.x * initialScale, point.y * initialScale)

    path
  }

  def clear(): Unit = {
    paths.clear()
    repaint()
  }

  def calculateTransformation(): AffineTransform = {
    val lines = paths.values
    val maxX = lines.map(l => l.getBounds2D.getMaxX).max
    val maxY = lines.map(l => l.getBounds2D.getMaxY).max
    val minX = lines.map(l => l.getBounds2D.getMinX).min
    val minY = lines.map(l => l.getBounds2D.getMinY).min

    // println(s"Min: $minX x $minY, Max: $maxX, $maxY")

    val width = maxX - minX
    val height = maxY - minY

    val componentHeight = size.height - 2 * offset
    val componentWidth = size.width - 2 * offset
    val scaleH = if (height > componentHeight) componentHeight / height else 1.0
    val scaleW = if (width > componentWidth) componentWidth / width else 1.0

    val scale = if (scaleH < 1.0 || scaleW < 1.0) Math.min(scaleH, scaleW) else 1

    /*
      This corresponds to transformation matrix of
        Sx, 0, Tx
        0, Sy, Ty
        0,  0,  0
     */
    val af = new AffineTransform(
      scale, 0,
      0, scale,
      (-minX * scale) + offset, (-minY * scale) + offset
    )

    af
  }

  override def paint(g2: Graphics2D): Unit = {
    // White background
    g2.setColor(Color.CYAN)
    //g2.fillRect(0, 0, size.width, size.height)

    g2.setStroke(new BasicStroke(4))
    g2.setPaint(Color.DARK_GRAY)
    g2.drawString("Playing Field", 200, 100)
    g2.drawString(s"Time: $currentTime ms", 100, 100)

    if (paths.isEmpty) {
      // g2.setPaint(Color.RED)
      // g2.fill(pursuerIcon.createTransformedShape(AffineTransform.getTranslateInstance(150, 150)))


      // g2.setPaint(Color.BLUE)
      // val t = evaderIcon.createTransformedShape(AffineTransform.getTranslateInstance(180, 147))
      // g2.fill(t)
      return
    }

    val transformation = calculateTransformation()

    paths.foreach{ typeWithPath =>
      g2.setPaint(colors.getOrElse(typeWithPath._1.name, Color.ORANGE))
      val scaled = typeWithPath._2.createTransformedShape(transformation)
      // println(s"Drawing path at ${bounds.getMinX}x${bounds.getMinY} to ${bounds.getMaxX}x${bounds.getMaxY}")
      g2.draw(scaled)

      /*
      val icon = if (typeWithPath._1.name == "Evader") evaderIcon else pursuerIcon
      val iconTransform = transformation.clone().asInstanceOf[AffineTransform]
      iconTransform.rotate(orientations.getOrElse(typeWithPath._1.name, 0))
      iconTransform.translate(
        iconTransform.getTranslateX + typeWithPath._2.getCurrentPoint.getX,
        iconTransform.getTranslateY + typeWithPath._2.getCurrentPoint.getY
      )
      val scaledIcon = icon.createTransformedShape(iconTransform)
      g2.fill(scaledIcon)
      */
    }


  }

}

case class PathSegment(vehicleSettings: VehicleSettings, point: Vector3r, orientation: Quaternionr, time: Long)
