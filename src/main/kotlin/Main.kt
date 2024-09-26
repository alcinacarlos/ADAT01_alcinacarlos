import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun convertToDouble(value: String): Double {
    // Eliminar puntos que separan miles y reemplazar la coma decimal por un punto
    val cleanedValue = value.replace(".", "").replace(",", ".")
    return cleanedValue.toDouble()
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
fun statsToCSV(data: Map<String, List<String>>, outputFilePath: Path) {

}

fun main() {
    val filePath = Path.of("src", "main", "resources", "cotizacion.csv")
    val outputFilePath = Path.of("src","main","resources","output.csv")

    val columnData = CSVToColumns(filePath)
}