import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.DummyTell;

public class TestTell implements DummyTell {

	private static Logger logger = LoggerFactory.getLogger(TestTell.class);

	@Override
	public void tell(ObjectNode message) {
		logger.debug(message.toPrettyString());
	}
}
