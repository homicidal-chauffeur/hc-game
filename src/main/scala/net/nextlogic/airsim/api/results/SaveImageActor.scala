package net.nextlogic.airsim.api.results

import java.awt.{BasicStroke, Color}
import java.awt.geom.Line2D
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, ActorLogging}
import net.nextlogic.airsim.api.results.SaveImageActor._
import org.jfree.graphics2d.svg.{SVGGraphics2D, SVGUtils}

object SaveImageActor {
  case class SaveLines(lines: Seq[Line2D])
}

class SaveImageActor extends Actor with ActorLogging {
  val colors: Vector[Color] = Vector(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW)

  override def receive: Receive = {
    case SaveLines(lines) =>
      val height = lines.map(l => l.getBounds2D.getHeight).max
      val width = lines.map(l => l.getBounds2D.getWidth).max
      val g2 = new SVGGraphics2D(height.toInt, width.toInt)
      g2.setStroke(new BasicStroke(4))

      lines.zip(colors).foreach{ lineWithColor =>
        g2.setPaint(lineWithColor._2)
        g2.draw(lineWithColor._1)
      }

      val t = LocalTime.now()
      val f = new File(s"${t.formatted("YY-MM-dd-HH-mm-ss")}.svg")
      SVGUtils.writeToSVG(f, g2.getSVGElement)
  }
}
