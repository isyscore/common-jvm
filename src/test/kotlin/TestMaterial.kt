import com.isyscore.kotlin.common.createEntity
import com.isyscore.kotlin.common.inList
import com.isyscore.kotlin.common.useMap
import org.junit.Test
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect

class TestMaterial {


    var db: Database = Database.connect("jdbc:mariadb://10.30.30.148:23306/isc_ecology_model_prod", "org.mariadb.jdbc.Driver", "root", "ZljIsysc0re123", MySqlDialect())

    @Test
    fun test() {
        val list = db.useConnection { conn ->
            conn.prepareStatement(SQL).executeQuery().useMap { createEntity<MaterielVO>(it, strict = true) }
        }
        println(list)
    }
}

data class MaterielVO(
    var id: Long? = null,
    var materielName: String? = null,       // m.name
    var materielType: String? = null,       // m.type
    var materielId: Long? = null,              // m.id
    var materielCode: String? = null,       // t.materiel_code
    var desc: String? = null,               // t.desc
    var unit: String? = null,               // t.unit
    var specification: String? = null,      // t.specification
    var addressCode: String? = null,        // t.address_code
    var rentalCode: String? = null,         // t.rental_code
    var createBy: String? = null,           // t.create_by
    var createByName: String? = null,       // t.create_by_name
    var createTime: String? = null,         // t.create_time
    var status: String? = null,             // t.status
    var materielTypeId: Long? = null,           // t.id
    var materielTypeCode: String? = null,
    var type:String? = null
)

const val SQL = """
select m.name           as materielName,
       m.type           as materielType,
       m.id             as materielId,
       t.materiel_code  as materielCode,
       t.desc           as `desc`,
       t.unit           as unit,
       t.specification  as specification,
       t.address_code   as addressCode,
       t.rental_code    as rentalCode,
       t.create_by      as createBy,
       t.create_by_name as createByName,
       t.create_time    as createTime,
       t.status         as status,
       t.type           as type,
       t.id,
       t.id             as materielTypeId,
       t.code           as materielTypeCode
from t_base_materiel_zkmmbvjbdzxbp m left join t_base_materiel_type_vcqbfznnvyyoo t on m.code = t.materiel_code
where m.del_flag = '0' and t.del_flag = '0'; 
"""