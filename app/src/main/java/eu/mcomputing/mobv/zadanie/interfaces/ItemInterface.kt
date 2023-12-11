package eu.mcomputing.mobv.zadanie.interfaces

import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity

interface ItemInterface {
    fun onItemClick(userEntity: UserEntity)
}