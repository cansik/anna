package ch.bildspur.anna.model.ann

import com.google.gson.annotations.Expose

class Network(@Expose val layers : MutableList<Layer> = mutableListOf(),
              @Expose val weights : MutableList<Weight> = mutableListOf()) {
}