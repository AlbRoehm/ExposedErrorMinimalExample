package com.example.exposed.transaction

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RandomTable : IntIdTable() {
    var test = text("text")
}


class RandomEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RandomEntity>(RandomTable)

    var test by RandomTable.test
}
