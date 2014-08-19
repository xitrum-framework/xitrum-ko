This is a plugin for `Xitrum <http://xitrum-framework.github.io/>`_ that
provides some convenient helpers to work with `Knockout.js <http://knockoutjs.com/>`_.

Config your Xitrum project to use this plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Edit your Xitrum project's build.sbt:

::

  libraryDependencies += "tv.cntt" %% "xitrum-ko" % "1.7"

Usage
~~~~~

See Knockout.js demo in the project `xitrum-demos <https://github.com/xitrum-framework/xitrum-demos>`_.

Assume that you are using `Scalate template engine <https://github.com/xitrum-framework/xitrum-scalate>`_.

1.

Include Knockout's JS files (knockout.js and knockout.mapping.js) to your template:

::

  import xitrum.Knockout

  != Knockout.js

2.

In the template, call helpers that you want to use:

::

  Knockout.applyBindingsCs(RVTodoList.get, classOf[TodosSave], """
    $('#new_todo_form').submit ->
      if $('#new_todo_form').valid()
        desc = $('#new_todo_desc').val()
        $('#new_todo_desc').val('')
        todo = {done: false, desc: desc}
        model.todos.push(ko.mapping.fromJS(todo))
      false

    $('#save').click(sync)
  """)

``Cs`` in ``applyBindingsCs`` means that you will give the helper a CoffeeScript
snippet. If you want to use JS, use ``applyBindingsJs``.

Helpers:

* ``applyBindingsCs(model: AnyRef, syncActionClass: Class[_ <: Action], cs: String)``
* ``applyBindingsCs(model: AnyRef, scopeSelector: String, syncActionClass: Class[_ <: Action], cs: String)``
* ``applyBindingsJs(model: AnyRef, syncActionClass: Class[_ <: Action], js: String)``
* ``applyBindingsJs(model: AnyRef, scopeSelector: String, syncActionClass: Class[_ <: Action], js: String)``
