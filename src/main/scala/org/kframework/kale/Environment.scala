package org.kframework.kale

import org.kframework.kale.context.{AnywhereContextApplicationLabel, PatternContextApplicationLabel}

import scala.collection.mutable

/**
  * Created by cos on 3/15/17.
  */
case class Environment() {
  val uniqueLabels = mutable.Map[String, Label]()

  def labels = uniqueLabels.values.toSet

  private var pisSealed = false

  def seal(): Unit = pisSealed = true

  def isSealed = pisSealed

  def register(label: Label): Int = {
    assert(!isSealed, "The environment is sealed")
    assert(label != null)

    if (uniqueLabels.contains(label.name))
      throw new AssertionError("Label " + label.name + " already registered. The current env is: \n" + this)

    uniqueLabels.put(label.name, label)
    uniqueLabels.size
  }

  def label(labelName: String): Label = uniqueLabels(labelName)

  override def toString = {
    "nextId: " + uniqueLabels.size + "\n" + uniqueLabels.mkString("\n")
  }

  implicit private val tthis = this

  val Variable = VariableLabel()

  val Truth = TruthLabel()

  val Hole = Variable("☐")

  val Top: Truth with Substitution = TopInstance()
  val Bottom: Truth = BottomInstance()

  val Equality = EqualityLabel()
  val And = AndLabel()
  val Or = OrLabel()
  val Rewrite = RewriteLabel()

  val AnywhereContext = AnywhereContextApplicationLabel()
  val CAPP = PatternContextApplicationLabel("CAPP")

  val builtin = new Builtins()(this)

  def bottomize(_1: Term)(f: => Term): Term = {
    if (Bottom == _1)
      Bottom
    else
      f
  }

  def bottomize(_1: Term, _2: Term)(f: => Term): Term = {
    if (Bottom == _1 || Bottom == _2)
      Bottom
    else
      f
  }

  def bottomize(terms: Term*)(f: => Term): Term = {
    if (terms.contains(Bottom))
      Bottom
    else
      f
  }

  def renameVariables[T <: Term](t: T): T = {
    val rename = And.substitution((Util.variables(t) map (v => (v, v.label(v.name + Math.random().toInt)))).toMap)
    rename(t).asInstanceOf[T]
  }
}
