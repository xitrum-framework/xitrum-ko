package xitrum

import xitrum.util.{CoffeeScriptCompiler, SeriDeseri}

object Knockout {
  def js(implicit action: Action) = {
    <xml:group>
      <script type="text/javascript" src={action.webJarsUrl("knockoutjs/3.4.0/dist",                "knockout.debug.js",                "knockout.js")}></script>
      <script type="text/javascript" src={action.webJarsUrl("knockout-mapping/2.4.1/build/output", "knockout.mapping-latest.debug.js", "knockout.mapping-latest.js")}></script>
    </xml:group>
  }

  def applyBindingsCs(model: AnyRef, syncActionClass: Class[_ <: Action], cs: String)(implicit action: Action) {
    applyBindingsCs(model, None, syncActionClass, cs)
  }

  def applyBindingsCs(model: AnyRef, scopeSelector: String, syncActionClass: Class[_ <: Action], cs: String)(implicit action: Action) {
    applyBindingsCs(model, Some(scopeSelector), syncActionClass, cs)
  }

  def applyBindingsJs(model: AnyRef, syncActionClass: Class[_ <: Action], js: String)(implicit action: Action) {
    applyBindingsJs(model, None, syncActionClass, js)
  }

  def applyBindingsJs(model: AnyRef, scopeSelector: String, syncActionClass: Class[_ <: Action], js: String)(implicit action: Action) {
    applyBindingsJs(model, Some(scopeSelector), syncActionClass, js)
  }

  //----------------------------------------------------------------------------

  private def applyBindingsCs(model: AnyRef, scopeSelector: Option[String], syncActionClass: Class[_ <: Action], cs: String)(implicit action: Action) {
    val js = CoffeeScriptCompiler.compile(cs).get
    applyBindingsJs(model, scopeSelector, syncActionClass, js)
  }

  private def applyBindingsJs(model: AnyRef, scopeSelector: Option[String], syncActionClass: Class[_ <: Action], js: String)(implicit action: Action) {
    // jQuery automatically converts Ajax response based on content type header
    val prepareModel =
      "var model = ko.mapping.fromJS(" + SeriDeseri.toJson(model) + ");\n" +
      (if (scopeSelector.isEmpty) "ko.applyBindings(model);\n" else "ko.applyBindings(model, " + scopeSelector + "[0]);\n")
    val prepareSync =
      "var sync = function() {\n" +
        "$.post('" + Config.routes.reverseMappings(syncActionClass.getName).url(Map.empty) + """', {model: ko.mapping.toJSON(model)}, function(data) {
          if (typeof(data) === 'object') {
            model = ko.mapping.fromJS(data);
            ko.applyBindings(model);
          }
        });
        return false;
      };
      var syncIfValid = function(formSelector) {
        return (function() {
          if (formSelector.valid()) sync();
          return false;
        });
      };"""
    action.jsAddToView(
      "(function () {\n" +
        prepareModel +
        prepareSync +
        js +
      "})();"
    )
  }
}
