package malayalamdictionary.samasya.domain.firebase

import com.fasterxml.jackson.annotation.JsonProperty

data class AppUpdate(
    @field:JsonProperty("title")
    val title: String ? = null,

    @field:JsonProperty("description")
    val description: String ? = null,

    @field:JsonProperty("version")
    val version: String ? = null,

    @field:JsonProperty("force_update")
    val forceUpdate: Boolean = false
)