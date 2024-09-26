import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

fun convertToDouble(value: String): Double {
    // Eliminar puntos que separan miles y reemplazar la coma decimal por un punto
    val cleanedValue = value.replace(".", "").replace(",", ".")
    return cleanedValue.toDouble()
}
fun isNum(input: String): Boolean {
    val numero = input.replace(".", "").replace(",", ".")
    return when{
        numero.toIntOrNull() != null -> true
        numero.toDoubleOrNull() != null -> true
        numero.toLongOrNull() != null -> true
        else -> false
    }
}
fun CSVToColumns(filePath: Path): Map<String, List<String>>{
    val br = Files.newBufferedReader(filePath)
    val rows = mutableListOf<List<String>>()
    br.use { bufferedReader ->
        bufferedReader.forEachLine { line ->
            val columns = line.split(";")
            rows.add(columns)
        }
    }
    val column = mutableMapOf<String, List<String>>()
    val headers = rows.first()
    for (i in headers.indices){
        val listaDatos = mutableListOf<String>()
        for (row in rows.drop(1)){
            listaDatos.add(row[i])
        }
        column[headers[i]] = listaDatos
    }
    return column
}
fun statsToCSV(data: Map<String, List<String>>, path: Path) {
    Files.write(path, "Columna;Mínimo;Máximo;Media\n".toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.APPEND)

    for ((key, value) in data) {
        var lineToWrite = ""
        val valueDouble = mutableListOf<Double>()
        value.forEach { item ->
            if (isNum(item)){
                valueDouble.add(convertToDouble(item))
            }
        }
        if (valueDouble.size > 0){
            val min = valueDouble.min()
            val max = valueDouble.max()
            val average = valueDouble.average()
            lineToWrite = "${key};${min};${max};${"%.2f".format(average)}\n"
            Files.write(path, lineToWrite.toByteArray(), StandardOpenOption.APPEND)
        }

    }
}

fun main() {
    val filePath = Path.of("src", "main", "resources", "cotizacion.csv")
    val outputFilePath = Path.of("src","main","resources","output.csv")

    val columnData = CSVToColumns(filePath)
    statsToCSV(columnData, outputFilePath)
}