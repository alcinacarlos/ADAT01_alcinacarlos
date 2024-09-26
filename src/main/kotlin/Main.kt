import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.notExists

fun convertToDouble(value: String): Double {
    // Eliminar puntos que separan miles y reemplazar la coma decimal por un punto
    val cleanedValue = value.replace(".", "").replace(",", ".")
    return cleanedValue.toDouble()
}

fun isNum(input: String): Boolean {
    val numero = input.replace(".", "").replace(",", ".")
    return when {
        numero.toIntOrNull() != null -> true
        numero.toDoubleOrNull() != null -> true
        numero.toLongOrNull() != null -> true
        else -> false
    }
}

fun csvToColumns(filePath: Path): Map<String, List<String>> {
    val br = Files.newBufferedReader(filePath)
    if (filePath.notExists()) throw FileNotFoundException("No existe el fichero ${filePath}")
    val rows = mutableListOf<List<String>>()
    br.use { bufferedReader ->
        bufferedReader.forEachLine { line ->
            val columns = line.split(";")
            rows.add(columns)
        }
    }
    val column = mutableMapOf<String, List<String>>()
    val headers = rows.first()
    for (i in headers.indices) {
        val listaDatos = mutableListOf<String>()
        for (row in rows.drop(1)) {
            listaDatos.add(row[i])
        }
        column[headers[i]] = listaDatos
    }
    return column
}

fun statsToCSV(data: Map<String, List<String>>, path: Path) {
    Files.write(
        path,
        "Columna;Mínimo;Máximo;Media\n".toByteArray(),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
    )

    for ((key, value) in data) {
        val valueDouble = mutableListOf<Double>()
        value.forEach { item ->
            if (isNum(item)) {
                valueDouble.add(convertToDouble(item))
            }
        }
        if (valueDouble.size > 0) {
            val min = valueDouble.min()
            val max = valueDouble.max()
            val average = valueDouble.average()
            val lineToWrite = "${key};${"%.2f".format(min)};${"%.2f".format(max)};${"%.2f".format(average)}\n"
            Files.write(path, lineToWrite.toByteArray(), StandardOpenOption.APPEND)
        }

    }
}

fun main() {
    val filePath = Path.of("src", "main", "resources", "cotizacion.csv")
    val outputFilePath = Path.of("src", "main", "resources", "output.csv")

    val columnData = csvToColumns(filePath)
    statsToCSV(columnData, outputFilePath)
}