package ch.bildspur.anna.model.ann

import ch.bildspur.anna.model.light.LedArray
import com.google.gson.annotations.Expose

class Neuron(@Expose val ledArray : LedArray = LedArray()) {
}