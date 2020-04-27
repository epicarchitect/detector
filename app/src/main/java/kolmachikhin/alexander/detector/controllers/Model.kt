package kolmachikhin.alexander.detector.controllers

import kolmachikhin.alexander.detector.constants.VOID_ID

open class Model(open val id: Int) {

    val isVoid get() = id == VOID_ID

    val isImmutable get() = id < VOID_ID

}