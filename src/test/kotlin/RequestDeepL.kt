data class ReqDeepL(
//    val id: Int,
    val jsonrpc: String = "2.0",
    val method: String = "LMT_handle_jobs",
    val params: Params = Params()
)

data class Params(
    val commonJobParams: CommonJobParams = CommonJobParams(),
    val jobs: List<Job> = listOf(Job()),
    val lang: Lang = Lang(),
    val priority: Int = -1,
    val timestamp: Long = System.currentTimeMillis()
)

class CommonJobParams(
)

data class Job(
    val kind: String = "default",
    val preferred_num_beams: Int = 4,
    val quality: String = "fast",
    val raw_en_context_after: List<Any> = listOf(),
    val raw_en_context_before: List<Any> = listOf(),
    var raw_en_sentence: String = ""
)

data class Lang(
    val source_lang_user_selected: String = "ZH",
    val target_lang: String = "JA",
    val user_preferred_langs: List<String> = listOf("EN","JA","ZH")
)