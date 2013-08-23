package z1223838293847182.x03849283784.p2pinternet.net;

import java.io.IOException;

import java.io.OutputStream;

import javax.crypto.Cipher;

public class VerifiedOutputStream extends OutputStream {

	public static class TLS {
		
		public static byte[] append(byte[]... arr)
		{
			int tLen = 0;
			for (byte[] a : arr)
				tLen += a.length;
			byte[] result = new byte[tLen];
			int i = 0;
			for (byte[] a : arr)
			{
				System.arraycopy(a, 0, result, i, a.length);
				i += a.length;
			}
			return result;
		}
		
		public static byte[] XOR(byte[] x, byte[] y)
		{
			if (x.length != y.length) return null;
			byte[] z = new byte[x.length];
			for (int i = 0; i < z.length; i++)
				z[i] = (byte) (x[i] ^ y[i]);
			return z;
		}
		
		public static byte[] PRF(byte[] secret, byte[] label, byte[] seed)
		{
			int len = (int) Math.round(Math.ceil(secret.length/2.0));
			byte[] h1 = new byte[len];
			byte[] h2 = new byte[len];
			System.arraycopy(secret, 0, h1, 0, len);
			if (len * 2 > secret.length)
			{
				System.arraycopy(secret, len-1, h2, 0, len);
			} else {
				System.arraycopy(secret, len, h2, 0, len);
			}
			h1 = P(h1, seed, 5, "MD5");
			h2 = P(h2, seed, 4, "SHA-1");
			return XOR(h1, h2);
		}
		
		public static byte[] P(byte[] secret, byte[] seed, int x, String h)
		{
			byte[][] arrays = new byte[x][];
			byte[][] A = new byte[x+1][];
			A[0] = seed;
			for (int i = 0; i < x; i++)
			{
				A[i+1] = A(secret, A[i], h);
				arrays[i] = HMAC_hash(secret, append(A[i+1], seed), h);
			}
			return append(arrays);
		}
		
		private static byte[] A(byte[] secret, byte[] iv, String h)
		{
			return HMAC_hash(secret, iv, h);
		}
		public static byte[] HMAC_hash(byte[] key, byte[] msg, String h)
		{
			try {
				Cipher cip = Cipher.getInstance(h);
				int blockSize = cip.getBlockSize();
				if (key.length > blockSize)
				{
					cip.update(key);
					key = cip.doFinal();
					cip = Cipher.getInstance(h);
				}

				if (key.length < blockSize)
				{
					int diff = blockSize - key.length;
					byte[] app = new byte[diff];
					key = append(key, app);
				}
				byte[] o_key_pad = new byte[key.length];
				byte[] i_key_pad = new byte[key.length];
				for (int i = 0; i < key.length; i++)
				{
					o_key_pad[i] = 0x5c;
					i_key_pad[i] = 0x36;
				}
				byte[] m1 = append(i_key_pad, msg);
				cip.update(m1);
				m1 = cip.doFinal();
				cip = Cipher.getInstance(h);
				m1 = append(o_key_pad, m1);
				cip.update(m1);
				return cip.doFinal();
			} catch (Throwable t){throw new RuntimeException(t);}
		}
		
	}
	private OutputStream out;
	public VerifiedOutputStream(OutputStream o)
	{
		out = o;
	}
	@Override
	public void write(int arg0) throws IOException {
		out.write(arg0);
	}
	
	public void write(byte[] b) throws IOException {
		out.write(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
	

}
