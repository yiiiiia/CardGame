import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;
import utils.ImageListForPreLoad;

/**
 * This is an example of a JUnit test. In this case, we want to be able to test
 * the logic of our system without needing to actually start the web server. We
 * do this by overriding the altTell method in BasicCommands, which means
 * whenever a command would normally be sent to the front-end it is instead
 * discarded. We can manually simulate messages coming from the front-end by
 * calling the processEvent method on the appropriate event processor.
 * 
 * @author Richard
 *
 */
public class InitalizationTest {

	/**
	 * This test simply checks that a boolean vairable is set in GameState when we
	 * call the initalize method for illustration.
	 */
	@Test
	public void checkImagePreload() {
		String cardsDIR = "conf/gameconfs/cards/1_1_c_u_bad_omen.json";
		List<String> images = ImageListForPreLoad.getCardImagesForPreload(cardsDIR);
		System.out.println("hello,world");
		assertTrue(!images.isEmpty());
	}

}
