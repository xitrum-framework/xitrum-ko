package xitrum

import xitrum.util.{CoffeeScriptCompiler, SeriDeseri}

object Knockout {
  def js(implicit action: Action) = {
    if (Config.productionMode)
      <xml:group>
        <script type="text/javascript" src={action.resourceUrl("xitrum-ko/knockout-3.1.0.min.js")}></script>
        <script type="text/javascript" src={action.resourceUrl("xitrum-ko/knockout.mapping-2.4.1.min.js")}></script>
      </xml:group>
    else
      <xml:group>
        <script type="text/javascript" src={action.resourceUrl("xitrum-ko/knockout-3.1.0.js")}></script>
        <script type="text/javascript" src={action.resourceUrl("xitrum-ko/knockout.mapping-2.4.1.js")}></script>
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
        "$.post('" + Config.routesReverseMappings(syncActionClass).url(Map()) + """', {model: ko.mapping.toJSON(model)}, function(data) {
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
