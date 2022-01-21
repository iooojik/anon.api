package iooojik.dev.anon.api.database

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "bans")
data class Ban(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "_id", insertable = false, updatable = false)
    var id: Long = -1,
    @Column(name = "isBanned")
    val isBanned: Boolean = false,
    @Column(name = "reason")
    val reason: String = "",
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserModel? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Ban

        return id > -1 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , isBanned = $isBanned , reason = $reason )"
    }
}