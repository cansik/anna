package ch.bildspur.anna.view.util

class TagItem(val item: Any? = null, val name: String = "TagItem") {

    override fun toString(): String {
        if (item != null)
            return item.toString()
        else
            return name
    }
}