package com.mabrcosta.keysmanager.users.business

import cats.data.Reader
import com.mabrcosta.keysmanager.core.data.EntityId
import com.mabrcosta.keysmanager.users.data.User
import org.atnos.eff.|=

package object api {

  type OwnerReader[A] = Reader[EntityId[User], A]
  type _ownerReader[R] = OwnerReader |= R

  type KeysErrorEither[A] = KeysError Either A
  type _keysErrorEither[R] = KeysErrorEither |= R

  type UsersErrorEither[A] = UsersError Either A
  type _usersErrorEither[R] = UsersErrorEither |= R

  type UsersGroupsErrorEither[A] = UsersGroupsError Either A
  type _usersGroupsErrorEither[R] = UsersGroupsErrorEither |= R

}
