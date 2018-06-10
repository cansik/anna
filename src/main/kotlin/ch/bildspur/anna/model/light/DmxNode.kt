package ch.bildspur.anna.model.light

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.properties.StringParameter
import com.google.gson.annotations.Expose
import java.util.concurrent.CopyOnWriteArrayList

class DmxNode(@StringParameter("Address") @Expose var address: DataModel<String> = DataModel("127.0.0.1"),
              @Expose var universes: CopyOnWriteArrayList<DmxUniverse> = CopyOnWriteArrayList()) {
    override fun toString(): String {
        return "Node (${address.value})"
    }
}