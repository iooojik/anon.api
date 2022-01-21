package iooojik.dev.anon.api.database

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "filters")
data class FilterModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "_id", insertable = false, updatable = false)
    var id: Long = -1,
    @Column(name = "my_age")
    var myAge: Int = 18,
    @Column(name = "interlocutorAges")
    var interlocutorAges: String = "$myAge/$myAge",
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserModel? = null,
    @Column(name = "interlocutorSex")
    var interlocutorSex: String = "male",
    @Column(name = "mySex")
    var mySex: String = "male",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as FilterModel

        return id > 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , myAge = $myAge , interlocutorAges = $interlocutorAges , interlocutorSex = $interlocutorSex , mySex = $mySex )"
    }
}
