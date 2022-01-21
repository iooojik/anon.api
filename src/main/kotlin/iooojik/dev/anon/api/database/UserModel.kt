package iooojik.dev.anon.api.database

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
data class UserModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "_id", insertable = false, updatable = false)
    var id: Long = -1,
    @Column(name = "uuid")
    var uuid: String = "",
    @Column(name = "login")
    var userLogin: String = "",
    @Column(name = "lastLogin")
    var lastLogin: String = Date().toString(),
    @Column(name = "birthDate")
    var birthDate: String = "",
    @Column(name = "password")
    var password: String = "",
    @OneToOne(
        mappedBy = "user", fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JsonManagedReference
    var banned: Ban? = Ban(),
    @Column(name = "positive_reputation")
    var positiveReputation: Int? = 0,
    @Column(name = "negative_reputation")
    var negativeReputation: Int? = 0,
    @OneToOne(
        mappedBy = "user", fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JsonManagedReference
    var filter: FilterModel = FilterModel(),
    @ManyToMany(
        cascade = [CascadeType.ALL]
    )
    @JsonBackReference
    var stack: MutableSet<SearchStackModel>? = mutableSetOf(),
    @OneToMany(
        mappedBy = "author", fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JsonIgnore
    var messages: List<MessageModel>? = listOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserModel

        return id > -1 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , uuid = $uuid , userLogin = $userLogin , lastLogin = $lastLogin , birthDate = $birthDate , password = $password , positiveReputation = $positiveReputation , negativeReputation = $negativeReputation )"
    }
}
