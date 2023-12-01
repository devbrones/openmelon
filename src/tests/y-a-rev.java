protected void conversionMethod(short[] rawSamples) {
    int[] Sample1 = new int[2];
    int[] Sample2 = new int[2];
    int[] Sample3 = new int[2];
    byte skipBit = 0;
    for (byte i = 0; skipBit < 18; i++) { // use skipBit to iterate through the 18 hex values in the array
      for (byte j = 0; j < 2; j++) { // iterate throught the 3 samples

        short[] tempSample = new short[3];
        for (int loop = 0; loop < 3; loop++) // iterate through the 3 bytes in each sample
          tempSample[loop] = rawSamples[skipBit + loop + j * 3]; // 
        
        int val1 = tempSample[0] & 0xFF; // convert to unsigned int
        int val2 = tempSample[1] & 0xFF; // 
        int val3 = tempSample[2] & 0xFF; // 
        int sampleValue = (val1 << 16) + (val2 << 8) + val3; // combine the 3 bytes into one int


        if (i == 0) {    // write to the right sample array
          Sample1[j] = sampleValue;
        } else if (i == 1) {
          Sample2[j] = sampleValue;
        } else if (i == 2) {
          Sample3[j] = sampleValue;
        } 
      } 
      skipBit += 6;
    } 
    float[] finalSample1 = multiplier(Sample1); // convert the int arrays to float arrays
    float[] finalSample2 = multiplier(Sample2); //
    float[] finalSample3 = multiplier(Sample3); //
}

private float[] multiplier(int[] sample) {
  float[] finalSample = new float[i];
  for (byte i = 0; i < sample.length; i++) {
    finalSample[i] = sample[i] * 0.4F / ((float)Math.pow(2.0D, 23.0D) - 1.0F) * 1000000.0F; // multiply by 0.4 / 2^23 - 1 * 1000000, why may you ask? I have no idea.
  } 
  return finalSample;
}