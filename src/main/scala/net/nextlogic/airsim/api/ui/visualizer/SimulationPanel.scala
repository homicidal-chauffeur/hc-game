package net.nextlogic.airsim.api.ui.visualizer

import java.awt.geom.{AffineTransform, Path2D}
import java.awt.{BasicStroke, Color, Dimension, Graphics2D}

import net.nextlogic.airsim.api.results.PathSegment
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._
import net.nextlogic.airsim.api.utils.Vector3r

import scala.collection.mutable
import scala.swing.Panel

class SimulationPanel extends Panel {
//  override lazy val peer = new SimulationJPanel()

  val colors: Map[PilotType, Color] = Map(
    Evade -> new Color(0, 0, 1, 0.2f),
    Pursue -> new Color(1, 0, 0, 0.2f)
  )
  var paths: mutable.Map[PilotType, Path2D] = mutable.Map[PilotType, Path2D]()
  val initialScale = 50.0
  val offset = 10

  preferredSize = new Dimension(1200, 800)

  def addSegment(segment: PathSegment): Path2D = {
    val path = paths.getOrElse(segment.pilotType, newPath(segment.point))
    path.lineTo(segment.point.x * initialScale, segment.point.y * initialScale)
    paths.update(segment.pilotType, path)
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
  }

  def calculateTransformation(): AffineTransform = {
    val lines = paths.values
    val maxX = lines.map(l => l.getBounds2D.getMaxX).max
    val maxY = lines.map(l => l.getBounds2D.getMaxY).max
    val minX = lines.map(l => l.getBounds2D.getMinX).min
    val minY = lines.map(l => l.getBounds2D.getMinY).min

    println(s"Min: $minX x $minY, Max: $maxX, $maxY")

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

    if (paths.isEmpty) return

    val transformation = calculateTransformation()

    paths.foreach{ typeWithPath =>
      g2.setPaint(colors.getOrElse(typeWithPath._1, Color.ORANGE))
      val scaled = typeWithPath._2.createTransformedShape(transformation)
      val bounds = scaled.getBounds2D
      println(s"Drawing path at ${bounds.getMinX}x${bounds.getMinY} to ${bounds.getMaxX}x${bounds.getMaxY}")
      g2.draw(scaled)
    }

  }

}
