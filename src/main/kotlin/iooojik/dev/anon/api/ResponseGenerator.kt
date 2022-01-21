package iooojik.dev.anon.api

import org.springframework.http.ResponseEntity

interface ResponseGenerator {

    fun okResponse(model : Any) : ResponseEntity<Any>{
        return ResponseEntity.ok().body(model)
    }

    fun errorResponse(message : String? = "", errorCode: Int = 403) : ResponseEntity<Any>{
        return ResponseEntity.status(errorCode).body(ErrorBody(message, errorCode))
    }

}

data class ErrorBody(val errorMessage : String? = "", val errorCode : Int? = 403)

