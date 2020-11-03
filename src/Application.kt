package org.simonsbacka

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOneById
import org.litote.kmongo.replaceOne
import org.simonsbacka.model.Monitorering
import org.simonsbacka.model.Monitoreringstillfalle
import java.text.DateFormat

const val API_ENDPOINT = "/v1"
const val MONITORERING_ENDPOINT = "$API_ENDPOINT/monitorering"
const val MONITORERINGTILLFALLE_ENDPOINT = "$API_ENDPOINT/monitoreringtillfalle"
const val MONITORERINGTILLFALLEN_ENDPOINT = "$API_ENDPOINT/monitoreringtillfallen"

val database: MongoDatabase = KMongo.createClient().getDatabase("monitorering")
val monitoreringCollection: MongoCollection<Monitorering> = database.getCollection("monitorering", Monitorering::class.java)
val monitoreringstillfalleCollection: MongoCollection<Monitoreringstillfalle> = database.getCollection("monitoreringtillfalle", Monitoreringstillfalle::class.java)

fun Application.main() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    routing {
        get("$MONITORERING_ENDPOINT/{systemId}") {
            val monitoreringar =
                monitoreringCollection.find(eq("systemId", call.parameters["systemId"]))
            call.respond(HttpStatusCode.OK, monitoreringar.toList())
        }
        post(MONITORERING_ENDPOINT) {
            val monitorering = call.receive<Monitorering>()
            monitoreringCollection.insertOne(monitorering)
            call.respond(HttpStatusCode.OK, monitorering)
        }
        get("$MONITORERINGTILLFALLEN_ENDPOINT/{monitoreringId}") {
            val monitoreringstillfallen =
                monitoreringstillfalleCollection.find(eq("monitoreringId", call.parameters["monitoreringId"]))
            call.respond(HttpStatusCode.OK, monitoreringstillfallen.toList())
        }
        get("$MONITORERINGTILLFALLE_ENDPOINT/{id}") {
            val monitoreringstillfalle =
                monitoreringstillfalleCollection.findOneById(call.parameters["id"]!!)!!
            call.respond(HttpStatusCode.OK, monitoreringstillfalle)
        }
        post(MONITORERINGTILLFALLE_ENDPOINT) {
            val monitoreringstillfalle = call.receive<Monitoreringstillfalle>()
            monitoreringstillfalleCollection.replaceOne(eq("_id", monitoreringstillfalle._id), monitoreringstillfalle, ReplaceOptions().upsert(true))
            call.respond(HttpStatusCode.OK, monitoreringstillfalle)
        }
    }
}

