package com.ximedes.vas.api.sync

import com.ximedes.vas.api.*
import com.ximedes.vas.domain.*
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.long
import org.http4k.lens.string
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.util.*

val curLens = Body.auto<CreateUserRequest>().toLens()
val carLens = Body.auto<CreateAccountRequest>().toLens()
val trLens = Body.auto<TransferRequest>().toLens()
val tLens = Body.auto<Transfer>().toLens()
val userIDLens = Path.string().of("userId")
val accListLens = Body.auto<List<Account>>().toLens()
val msLens = Query.long().optional("ms")
val resetLens = Body.auto<ResetRequest>().toLens()

fun main() {
    val ledger = SyncLedger()

    val app = routes(
        "/user" bind Method.POST to { request ->
            val msg = curLens.extract(request)
            val user = User(UserID(msg.id), msg.email)
            ledger.createUser(user)
            Response(OK)
        },
        "/account/{userId:.*}" bind Method.GET to { request ->
            val userID = UserID(userIDLens(request))
            val accounts = ledger.findAccountsByUserID(userID)
            accListLens.inject(accounts, Response(OK))
        },
        "/account" bind Method.POST to { request ->
            val msg = carLens.extract(request)
            val account = Account(UserID(msg.user), AccountID((msg.account)), 0, msg.overdraft, msg.description)
            ledger.createAccount(account)
            Response(OK)
        },
        "/transfer" bind Method.POST to { request ->
            val msg = trLens.extract(request)
            val id = TransferID(UUID.randomUUID().toString())
            val transfer = Transfer(id, AccountID(msg.from), AccountID(msg.to), msg.amount, msg.description)
            ledger.transfer(transfer)
            tLens.inject(transfer, Response(OK))
        },
        "/reset" bind Method.POST to { request ->
            val reset = resetLens.extract(request)
            val capacity = reset.readCapacityUnits?.let { rcu ->
                reset.writeCapacityUnits?.let { wcu ->
                    Pair(rcu, wcu)
                }
            }
            ledger.reset(capacity)
            Response(OK)
        },
        "/sleep" bind Method.GET to { request ->
            val ms = msLens.extract(request) ?: defaultSleepMS
            Thread.sleep(ms)
            Response(OK)
        }

    )

    app.asServer(Jetty(8080)).start()

}