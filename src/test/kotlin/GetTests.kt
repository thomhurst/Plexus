
import com.tomlonghurst.plexus.Plexus
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GetTests {
    @Test
    fun test1() {
        val plexusResponse = Plexus.get("https://reststop.randomhouse.com/resources/titles/9781400079148").response()

        Assert.assertTrue(plexusResponse.body.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><title"))
    }

    @Test
    fun test1_json() {
        val plexusResponse = Plexus.get("https://reststop.randomhouse.com/resources/titles/9781400079148").apply {
            header("Accept" to "application/json")
        }.response()

        Assert.assertTrue(plexusResponse.body.startsWith("""{"@uri":"https://reststop.randomhouse.com/resources/titles/9781400079148"""))
    }

    @Test
    fun test1_suspend() {
        runBlocking {
            val plexusResponse =
                Plexus.get("https://reststop.randomhouse.com/resources/titles/9781400079148").awaitResponse()

            Assert.assertTrue(plexusResponse.body.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><title"))
        }
    }

    @Test
    fun test1_json_suspend() {
        runBlocking {
            val plexusResponse = Plexus.get("https://reststop.randomhouse.com/resources/titles/9781400079148").apply {
                header("Accept" to "application/json")
            }.awaitResponse()

            Assert.assertTrue(plexusResponse.body.startsWith("""{"@uri":"https://reststop.randomhouse.com/resources/titles/9781400079148"""))
        }
    }
}