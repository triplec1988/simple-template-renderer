package triplec1988

import com.floreysoft.jmte._
import sbt.Keys._
import sbt._
import java.io.{PrintWriter, File}

import scala.collection.JavaConversions

/**
 * Created by chris on 9/25/15.
 */
object SimpleTemplateRenderer extends Plugin {

  // task key
  val generateTemplate = TaskKey[File]("gen-template")

  // setting
  val templateFile     = SettingKey[File]("template-file", "File to be rendered")

  val templateValues   = SettingKey[()=>Map[String, AnyRef]]("template-value", "Values to be substituted")

  // exported keys
  val tempSettings = inConfig(Compile)(Seq(
    generateTemplate <<= (streams,
      dependencyClasspath in Compile,
      runner in Compile,
      templateFile,
      templateValues) map { (s, cp, r, tf, tv) => {
        makeTemplateSubstitutions(s.log, cp, r, tf, tv)
      }
    }
  ))

  private final def makeTemplateSubstitutions(log: Logger, cp: Classpath, runner: ScalaRun, template: File, values: ()=>Map[String, AnyRef]) : File = {
    val engine = new Engine
    val renderedConfig  = File.createTempFile("jooq-config", ".xml")
    renderedConfig.deleteOnExit
    val writer = new PrintWriter(renderedConfig)
    val substitutions = JavaConversions.mapAsJavaMap(values.apply())
    writer.write(engine.transform(IO.read(template), substitutions))
    writer.close()
    renderedConfig
  }
}
