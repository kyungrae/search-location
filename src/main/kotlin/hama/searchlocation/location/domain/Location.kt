package hama.searchlocation.location.domain

class Location(
    tempName: String
) {
    val name: String = tempName
        .replace("<b>", "")
        .replace("</b>", "")
        .trim()

    override fun equals(other: Any?): Boolean {
        return other is Location && other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
