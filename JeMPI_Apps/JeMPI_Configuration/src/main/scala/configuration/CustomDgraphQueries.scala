package configuration

import java.io.{File, PrintWriter}
import scala.language.{existentials, postfixOps}


object CustomDgraphQueries {

  private val classLocation = "../JeMPI_LibMPI/src/main/java/org/jembi/jempi/libmpi/dgraph"
  private val custom_className = "CustomDgraphQueries"
  private val packageText = "org.jembi.jempi.libmpi.dgraph"

  def generate(config: Config): Any = {

    val classFile: String = classLocation + File.separator + custom_className + ".java"
    println("Creating " + classFile)
    val file: File = new File(classFile)
    val writer: PrintWriter = new PrintWriter(file)
    writer.println(
      s"""package $packageText;
         |
         |import io.vavr.Function1;
         |import org.apache.commons.lang3.StringUtils;
         |import org.jembi.jempi.shared.models.CustomDemographicData;
         |
         |import java.util.LinkedList;
         |import java.util.List;
         |import java.util.Map;
         |
         |import static org.jembi.jempi.libmpi.dgraph.DgraphQueries.runGoldenRecordsQuery;
         |
         |final class $custom_className {
         |
         |   static final List<Function1<CustomDemographicData, DgraphGoldenRecords>> DETERMINISTIC_LINK_FUNCTIONS =
         |      List.of(${getDeterministicFunctions(config.rules.link.get)});
         |""".stripMargin)

    if (config.rules.matchNotification.isDefined) {
      writer.println(
        s"""   static final List<Function1<CustomDemographicData, DgraphGoldenRecords>> DETERMINISTIC_MATCH_FUNCTIONS =
           |      List.of(${getDeterministicFunctions(config.rules.matchNotification.get)});
           |""".stripMargin)
    } else {
      writer.println(
        s"""   static final List<Function1<CustomDemographicData, DgraphGoldenRecords>> DETERMINISTIC_MATCH_FUNCTIONS =
           |      List.of();
           |""".stripMargin)
    }

    if (config.rules.link.get.deterministic.isDefined) {
      config.rules.link.get.deterministic.get.foreach((name, rule) => emitRuleTemplate(name, rule))
    }
    if (config.rules.link.isDefined && config.rules.link.get.probabilistic.isDefined) {
      config.rules.link.get.probabilistic.get.foreach((name, rule) => emitRuleTemplate(name, rule))
    }

    if (config.rules.matchNotification.isDefined && config.rules.matchNotification.get.deterministic.isDefined) {
      config.rules.matchNotification.get.deterministic.get.foreach((name, rule) => emitRuleTemplate(name, rule))
    }
    if (config.rules.matchNotification.isDefined && config.rules.matchNotification.get.probabilistic.isDefined) {
      config.rules.matchNotification.get.probabilistic.get.foreach((name, rule) => emitRuleTemplate(name, rule))
    }

    if (config.rules.link.isDefined && config.rules.link.get.deterministic.isDefined) {
      config.rules.link.get.deterministic.get.foreach((name, rule) => emitRuleFunction(name, rule))
    }
    if (config.rules.link.isDefined && config.rules.link.get.probabilistic.isDefined) {
      config.rules.link.get.probabilistic.get.foreach((name, rule) => emitRuleFunction(name, rule))
    }

    emitMergeCandidates()

    emitFindCandidates("Link", "DETERMINISTIC_LINK_FUNCTIONS", config.rules.link)

    if (config.rules.matchNotification.isDefined && config.rules.matchNotification.get.deterministic.isDefined) {
      config.rules.matchNotification.get.deterministic.get.foreach((name, rule) => emitRuleFunction(name, rule))
    }
    if (config.rules.matchNotification.isDefined && config.rules.matchNotification.get.probabilistic.isDefined && config.rules.matchNotification.get.probabilistic.isDefined) {
      config.rules.matchNotification.get.probabilistic.get.foreach((name, rule) => emitRuleFunction(name, rule))
    }
    emitFindCandidates("Match", "DETERMINISTIC_MATCH_FUNCTIONS", config.rules.matchNotification)

    writer.println(
      s"""   private $custom_className() {
         |   }
         |
         |}""".stripMargin)
    writer.flush()
    writer.close()


    def getDeterministicFunctions(allRules: AllRules): String = {
      allRules.deterministic.get
        .map((name, _) => s"""${" " * 14}CustomDgraphQueries::${Utils.snakeCaseToCamelCase(name.toLowerCase)},""".stripMargin)
        .mkString("\n")
        .trim
        .dropRight(1)
    }

    def emitFindCandidates(funcQualifier: String, filterList: String, rules: Option[AllRules]): Unit = {
      writer.println(
        s"""   static List<CustomDgraphGoldenRecord> find${funcQualifier}Candidates(
           |      final CustomDemographicData interaction) {
           |      var result = DgraphQueries.deterministicFilter($filterList, interaction);
           |      if (!result.isEmpty()) {
           |         return result;
           |      }
           |      result = new LinkedList<>();""".stripMargin)
      if (rules.isDefined && rules.get.probabilistic.isDefined) {
        rules.get.probabilistic.get.foreach((name, _) => {
          val filterName = Utils.snakeCaseToCamelCase(name.toLowerCase)
          val vars = "interaction"
          writer.println(s"""${" " * 6}mergeCandidates(result, $filterName($vars));""".stripMargin)
        })
      }
      writer.println(
        """      return result;
          |   }
          |""".stripMargin)
    }

    def emitRuleFunction(name: String, rule: Rule): Unit = {

      def expression(expr: Ast.Expression): String = {
        expr match {
          case Ast.Or(x) =>
            "("
              + (for (k <- x.zipWithIndex) yield if (k._2 == 0) expression(k._1) else " && " + expression(k._1)).mkString
              + ")"
          case Ast.And(x) =>
            "("
              + (for (k <- x.zipWithIndex) yield if (k._2 == 0) expression(k._1) else " || " + expression(k._1)).mkString
              + ")"
          case Ast.Not(x) =>
            "NOT (" + expression(x) + ")"
          case Ast.Match(variable, _) =>
            Utils.snakeCaseToCamelCase(variable.name) + "IsBlank"
          case Ast.Eq(variable) =>
            Utils.snakeCaseToCamelCase(variable.name) + "IsBlank"
          case _ =>
            "ERROR"
        }
      }

      val vars = for (v <- rule.vars) yield v
      val text = rule.text

      val functionName = Utils.snakeCaseToCamelCase(name.toLowerCase)
      if (vars.length == 1)
        val v = vars(0)
        writer.println(
          s"""   private static DgraphGoldenRecords $functionName(final CustomDemographicData demographicData) {
             |      if (StringUtils.isBlank(demographicData.${Utils.snakeCaseToCamelCase(v)})) {
             |         return new DgraphGoldenRecords(List.of());
             |      }
             |      final Map<String, String> map = Map.of("$$$v", demographicData.${Utils.snakeCaseToCamelCase(v)});
             |      return runGoldenRecordsQuery($name, map);
             |   }
             |""".stripMargin)
      else
        val expr = expression(ParseRule.parse(text))
        writer.println(s"   private static DgraphGoldenRecords $functionName(final CustomDemographicData demographicData) {")
        vars.foreach(v => {
          val camelCaseVarName = Utils.snakeCaseToCamelCase(v)
          writer.println(s"      final var $camelCaseVarName = demographicData.$camelCaseVarName;")
        })
        vars.foreach(v => {
          val camelCaseVarName = Utils.snakeCaseToCamelCase(v)
          val isNullVar = camelCaseVarName + s"IsBlank"
          writer.println(s"      final var $isNullVar = StringUtils.isBlank($camelCaseVarName);")
        })
        writer.print(
          s"""      if ($expr) {
             |         return new DgraphGoldenRecords(List.of());
             |      }
             |      final var map = Map.of(""".stripMargin)

        vars.zipWithIndex.foreach((v, idx) => {
          val camelCaseVarName = Utils.snakeCaseToCamelCase(v)
          writer.println(
            s"""${" " * (if (idx == 0) 0 else 29)}"$$$v",
               |${" " * 29}StringUtils.isNotBlank($camelCaseVarName)
               |${" " * 29}      ? $camelCaseVarName
               |${" " * 29}      : DgraphQueries.EMPTY_FIELD_SENTINEL${if (idx + 1 < vars.length) "," else ");"}""".stripMargin)
        })
        writer.println(
          s"""      return runGoldenRecordsQuery($name, map);
             |   }
             |""".stripMargin)
      end if
    }

    def emitRuleTemplate(name: String, rule: Rule): Unit = {

      val vars = for (v <- rule.vars) yield v
      val text = rule.text
      val expression: Ast.Expression = ParseRule.parse(text)
      val varsMap = vars.zipWithIndex.toMap[String, Int].map((k, i) => (k, ("A".head + i).toChar.toString))
      var meta = Map[String, (String, Option[Integer])]()

      def main_func(expression: Ast.Expression): String = {
        expression match {
          case Ast.Or(x) => "("
            + (for (k <- x.zipWithIndex) yield if (k._2 == 0) main_func(k._1) else " OR " + main_func(k._1)).mkString
            + ")"
          case Ast.And(x) => "("
            + (for (k <- x.zipWithIndex) yield if (k._2 == 0) main_func(k._1) else " AND " + main_func(k._1)).mkString
            + ")"
          case Ast.Not(x) =>
            "NOT (" + main_func(x) + ")"
          case Ast.Match(variable, distance) =>
            meta += (variable.name -> ("match", Option(distance)))
            "uid(" + variable.name + ")"
          case Ast.Eq(variable) =>
            meta += (variable.name -> ("eq", None))
            "uid(" + variable.name + ")"
          case _ =>
            "ERROR"
        }
      }

      def createScalerFunc(): Unit = {
        vars.foreach(v => {
          val fn = meta(v)._1
          writer.println(
            s"""${" " * 12}all(func:type(GoldenRecord)) @filter($fn(GoldenRecord.$v, $$$v${
              if (meta(v)._2.isDefined) ", " + meta(v)._2.get else
                ""
            })) {
               |${" " * 15}uid
               |${" " * 15}GoldenRecord.source_id {
               |${" " * 18}uid
               |${" " * 15}}""".stripMargin)
          if (config.uniqueGoldenRecordFields.isDefined) {
            config.uniqueGoldenRecordFields.get.foreach(field => {
              writer.println(s"${" " * 15}GoldenRecord.${field.fieldName}")
            })
          }
          config.demographicFields.foreach(field => {
            writer.println(s"${" " * 15}GoldenRecord.${field.fieldName}")
          })
          writer.println(s"${" " * 12}}")
        })
      }

      def createFilterFunc(all_func_str: String): Unit = {
        vars.foreach(v => {
          val fn = meta(v)._1
          writer.println(
            s"""${" " * 12}var(func:type(GoldenRecord)) @filter($fn(GoldenRecord.$v, $$$v${
              if (meta(v)._2.isDefined) ", " + meta(v)._2.get else ""
            })) {
               |${" " * 15}${varsMap(v)} as uid
               |${" " * 12}}""".stripMargin)
        })
        writer.println(
          s"""${" " * 12}all(func:type(GoldenRecord)) @filter${
            if (all_func_str.startsWith("(")) "" else "("
          }$all_func_str${if (all_func_str.startsWith("(")) "" else "("} {
             |${" " * 15}uid
             |${" " * 15}GoldenRecord.source_id {
             |${" " * 18}uid
             |${" " * 15}}""".stripMargin)
        if (config.uniqueGoldenRecordFields.isDefined) {
          config.uniqueGoldenRecordFields.get.foreach(field => {
            writer.println(s"${" " * 15}GoldenRecord.${field.fieldName}")
          })
        }
        config.demographicFields.foreach(field => {
          writer.println(s"${" " * 15}GoldenRecord.${field.fieldName}")
        })
        writer.println(s"${" " * 12}}")
      }

      var all_func_str = main_func(expression)
      varsMap.foreach((k, v) => {
        all_func_str = all_func_str.replace("uid(" + k + ")", "uid(" + v + ")")
      })

      writer.print(
        s"""${" " * 3}private static final String ${name.toUpperCase} =
           |${" " * 9}\"\"\"
           |${" " * 9}query ${name.toLowerCase}(""".stripMargin)

      vars.zipWithIndex.foreach((v, i) => {
        if (i > 0)
          writer.print(", ")
        end if
        writer.print(s"$$$v: string")
      })
      writer.println(") {")

      if (vars.length == 1)
        createScalerFunc()
      else
        createFilterFunc(all_func_str)
      end if
      writer.println(
        s"""${" " * 9}}
           |${" " * 9}\"\"\";
           |""".stripMargin)
    }

    def emitMergeCandidates(): Unit = {
      writer.println(
        s"""   private static void mergeCandidates(
           |         final List<CustomDgraphGoldenRecord> goldenRecords,
           |         final DgraphGoldenRecords block) {
           |      final var candidates = block.all();
           |      if (!candidates.isEmpty()) {
           |         candidates.forEach(candidate -> {
           |            var found = false;
           |            for (CustomDgraphGoldenRecord goldenRecord : goldenRecords) {
           |               if (candidate.goldenId().equals(goldenRecord.goldenId())) {
           |                  found = true;
           |                  break;
           |               }
           |            }
           |            if (!found) {
           |               goldenRecords.add(candidate);
           |            }
           |         });
           |      }
           |   }
           |""".stripMargin)
    }

  }

}
