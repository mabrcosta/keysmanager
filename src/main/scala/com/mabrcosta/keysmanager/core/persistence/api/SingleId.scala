package com.mabrcosta.keysmanager.core.persistence.api

trait SingleId[TEntity, TIdA] {

  val value: TIdA

}

