package net.nextlogic.airsim.api.results
import java.awt.{BasicStroke, Color, Graphics, Graphics2D}
import java.awt.geom.{AffineTransform, Path2D, Point2D}

import javax.swing.{JFrame, JPanel}
import net.nextlogic.airsim.api.simulators.settings.PilotSettings._

import net.nextlogic.airsim.api.utils.Vector3r

import scala.collection.mutable

object Visualizer {
  def apply(captureDistance: Double) = new Visualizer(captureDistance)
}

class Visualizer(captureDistance: Double) extends JPanel {
  val frame = new JFrame("Playing Field")
  // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setSize(1600, 1600)
  frame.add(this)
  frame.setVisible(true)

  val colors: Map[PilotType, Color] = Map(Evade -> Color.BLUE, Pursue -> Color.RED)

  var paths: mutable.Map[PilotType, Path2D] = mutable.Map[PilotType, Path2D]()

  def addSegment(segment: PathSegment): Path2D = {
    val path = paths.getOrElse(segment.pilotType, newPath(segment.point))
    val (x, y) = transformToBottomCenter(segment.point)
    path.lineTo(x, y)
    paths.update(segment.pilotType, path)

    path
  }

  def newPath(point: Vector3r): Path2D = {
    val path = new Path2D.Double()
    val (x, y) = transformToBottomCenter(point)
    path.moveTo(x, y)

    path
  }

  override def paint(g: Graphics) {
    // White background
    g.setColor(Color.gray)
    g.fillRect(0, 0, getWidth, getHeight)

    val g2 = g.asInstanceOf[Graphics2D]
    g2.setStroke(new BasicStroke(4))

    val (scaleW, scaleH) = calculateScale()

    paths.foreach{ typeWithPath =>
      g2.setPaint(colors.getOrElse(typeWithPath._1, Color.ORANGE))
      val af = new AffineTransform()
      af.setToScale(scaleW, scaleH)
      val scaled = typeWithPath._2.createTransformedShape(af)
      println(s"Drawing path of size ${scaled.getBounds2D.getWidth} x  ${scaled.getBounds2D.getHeight}")
      g2.draw(scaled)
    }

  }

  def calculateScale(): (Double, Double) = {
    val lines = paths.values
    val height = lines.map(l => l.getBounds2D.getMaxX).max
    val width = lines.map(l => l.getBounds2D.getMaxY).max

    (width / getWidth.toDouble, height / getHeight.toDouble)
  }

  def transformToBottomCenter(point: Vector3r): (Double, Double) =
    (getWidth / 2 - point.x * 50, getHeight - point.y * 50)

}

case class PathSegment(pilotType: PilotType, point: Vector3r)
