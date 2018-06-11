package ch.bildspur.anna.model.ann

import com.google.gson.annotations.Expose

class Layer(@Expose val neurons : MutableList<Neuron> = mutableListOf()) {
}