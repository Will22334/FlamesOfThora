import java.util.function.BiConsumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Function;
import com.thora.core.TestNetty;
import com.thora.core.net.netty.EncodingUtils;

import io.netty.buffer.ByteBuf;

class TestArrayEncoding extends TestNetty {

//	@BeforeAll
//	static void setUpBeforeClass() throws Exception {
//	}
	
	@Test
	public void test2DArray() {
		BiConsumer<String,ByteBuf> encoder = (s,b) -> EncodingUtils.writeVarString(s, b);
		
		
		int total = 15, x = 15;
		String[][] arr = new String[total][15];
		for(int j=0; j<x; ++j) {
			for(int i=0; i<total; ++i) {
				arr[j][i] = String.valueOf(i+1);
			}
		}
		
		ByteBuf buf = TestNetty.getBuf();
		
		EncodingUtils.encode2DObjectArrayNoIndex(buf, arr, encoder);
		
		Function<ByteBuf,String> decoder = EncodingUtils::readVarString;
		
		String[][] newArr = EncodingUtils.decode2DObjectArrayNoIndex(buf, String.class, decoder);
		
		Assertions.assertArrayEquals(arr, newArr);
		
	}
	
	@Test
	public void test1DArray() {
		final BiConsumer<String,ByteBuf> encoder = (s,b) -> EncodingUtils.writeVarString(s, b);
		
		
		final int size = 5;
		final String[] arr = new String[size];
		for(int i=0; i<size; ++i) {
			arr[i] = Integer.toString(i+1);
		}
		
		final ByteBuf buf = TestNetty.getBuf();
		
		EncodingUtils.encodeObjectArray(buf, arr, encoder);
		
		final Function<ByteBuf,String> decoder = EncodingUtils::readVarString;
		
		final String[] newArr = EncodingUtils.decodeObjectArray(buf, String.class, decoder);
		
		Assertions.assertArrayEquals(arr, newArr);
		
		//buf.release();
		
	}

}
