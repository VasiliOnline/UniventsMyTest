package com.example.univents.server.db

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

fun jooq(ds: DataSource): DSLContext = DSL.using(ds, SQLDialect.POSTGRES)
