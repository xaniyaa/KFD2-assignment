package ru.mephi

import kotlin.math.pow

class WrongOperationTypeException(message: String) : Exception(message)

class BadPropertyException(message: String) : Exception(message)

interface ConsoleService {
    fun work()
}

interface FigureService {
    fun addFigure(figure: Figure)
    fun getPerimeter(): Double
    fun getArea(): Double
}

sealed class Figure {
    abstract val figureProperty: Double
    abstract val perimeter: Double
    abstract val area: Double
}

class Square(sideLength: Double) : Figure() {
    override val figureProperty: Double = sideLength
    override val perimeter: Double = figureProperty * 4
    override val area: Double = figureProperty.pow(2)
}

class Circle(radiusValue: Double) : Figure() {
    override val figureProperty: Double = radiusValue
    override val perimeter: Double = 2 * Math.PI * figureProperty
    override val area: Double = Math.PI * figureProperty.pow(2)
}

enum class Operation() {
    INSERT, GET_AREA, GET_PERIMETER, EXIT;

    companion object {

        fun getOperation(line: String): Operation = when (line) {
            "1" -> INSERT
            "2" -> GET_AREA
            "3" -> GET_PERIMETER
            "4" -> EXIT
            else -> throw WrongOperationTypeException("Введен неизвестный тип операции: $line")
        }
    }
}


object ConsoleServiceImpl : ConsoleService {

    private var figureService: FigureService? = null

    init {
        if (figureService == null) {
            figureService = FigureServiceImpl
        }
    }

    private fun createFigure(inputArgs: List<String>): Figure {

        if (inputArgs.size != 2) {
            throw Exception("Can't create figure from {$inputArgs}")
        }

        val propertyValue: Double = inputArgs[1].toDouble()

        if ((propertyValue <= 0.0) || (propertyValue.isNaN())) {
            throw BadPropertyException("Введено неверное значение параметра property: $propertyValue")
        }

        return when (inputArgs[0]) {
            "Circle" -> Circle(radiusValue = propertyValue)
            "Square" -> Square(sideLength = propertyValue)
            else -> throw IllegalArgumentException("Unknown figure type")
        }
    }

    override fun work() {
        while (true) {
            println(
                "Введите тип операции, которую хотите исполнить:\n" +
                        "1) добавить фигуру\n" +
                        "2) получить площадь всех фигур\n" +
                        "3) получить периметр всех фигур\n" +
                        "4) завершить выполнение"
            )

            val option: String = readln()

            val operation: Operation = Operation.getOperation(option)
            when (operation) {
                Operation.INSERT -> {
                    println("Введите фигуру <название фигуры>(property=<какое-то вещественное значение>)")

                    var line: String = readln()
                    line = line.substring(0, line.length - 1)

                    val input: List<String> = line.split("(property=")
                    val figure: Figure = createFigure(input)
                    figureService?.addFigure(figure)
                }

                Operation.GET_AREA -> println("Площадь всех фигур: ${figureService?.getArea().toString()}")
                Operation.GET_PERIMETER -> println("Периметр всех фигур: ${figureService?.getPerimeter().toString()}")
                Operation.EXIT -> break
            }
        }
    }
}

object FigureServiceImpl : FigureService {

    private var figureList: MutableList<Figure> = ArrayList()

    override fun addFigure(figure: Figure) {
        figureList.add(figure)
    }

    override fun getPerimeter(): Double {
        var perimeter: Double = 0.0

        for (figure in figureList) {
            perimeter += figure.perimeter
        }

        return perimeter
    }

    override fun getArea(): Double {
        var area: Double = 0.0

        for (figure in figureList) {
            area += figure.area
        }

        return area
    }
}

fun main() {
    val consoleService = ConsoleServiceImpl
    try {
        consoleService.work()
    } catch (e: Exception) {
        println("[EXCEPTION] ${e.message}")
    }
}

