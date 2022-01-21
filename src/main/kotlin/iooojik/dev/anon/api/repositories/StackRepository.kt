package iooojik.dev.anon.api.repositories

import iooojik.dev.anon.api.database.SearchStackModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StackRepository : JpaRepository<SearchStackModel, Long>{
    fun findAllByAvailable(available: Boolean) : List<SearchStackModel>?
    //fun findAllByUserAndAvailable(userModel: UserModel, available: Boolean = true) : List<SearchStackModel>?
    fun findAllByUuid(uuid : String) : List<SearchStackModel>?
    //fun findAllByUser(userModel: UserModel) : List<SearchStackModel>?
}