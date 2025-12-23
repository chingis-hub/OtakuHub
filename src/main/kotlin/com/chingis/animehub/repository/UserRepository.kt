package com.chingis.animehub.repository

import com.chingis.animehub.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// JpaRepository — это интерфейс содержащий методы CRUD:
// save(), findById(), findAll(), deleteById()
//<User, Long> — это параметры дженерика:
//  User → какая сущность (таблица) управляется
//  Long → тип первичного ключа (id у User)
@Repository
interface UserRepository : JpaRepository<User, Long> {
}