package iooojik.dev.anon.api.database

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "messages")
data class MessageModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "_id", insertable = false, updatable = false)
    var id: Long = -1,
    @Column(name = "text")
    var text: String = "",
    @Column(name = "date")
    var date: String? = Date().toString(),
    @Column(name = "uuid")
    var uuid: String? = UUID.randomUUID().toString(),
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var author: UserModel? = null,
    @ManyToMany(mappedBy = "messages")
    @JsonBackReference
    var stackModels: List<SearchStackModel>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MessageModel

        return id > -1 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , text = $text )"
    }
}
