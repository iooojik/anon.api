package iooojik.dev.anon.api.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "search_stack")
data class SearchStackModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "_id", insertable = false, updatable = false)
    var id: Long = -1,
    @Column(name = "uuid")
    var uuid: String? = null,
    @Column(name = "available")
    var available: Boolean = true,
    @Column(name = "is_end")
    var isEnd: Boolean = false,
    @ManyToMany
    @JsonManagedReference
    var participants: MutableSet<UserModel>? = mutableSetOf(),
    @ManyToMany
    @JoinTable
    @JsonManagedReference
    var messages: List<MessageModel>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SearchStackModel

        return id > -1 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , uuid = $uuid , available = $available )"
    }
}