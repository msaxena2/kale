package org.kframework.kale

import scala.collection.Map

trait Substitution extends Term with (Term => Term) {
  def get(v: Variable): Option[Term]

  def apply(t: Term): Term

  def asMap: Map[Variable, Term]
}