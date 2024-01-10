import matplotlib.pyplot as plt
import datetime

def parse(byte_string):
    """
    The function takes a byte string as input, converts it to raw samples, extracts specific values from
    the samples, and multiplies them to obtain final samples.
    
    :param byte_string: The `byte_string` parameter is a string representing a sequence of hexadecimal
    values
    :return: The function `parse` returns three values: `final_sample1`, `final_sample2`,
    and `final_sample3`.
    """

    byte_array = bytes.fromhex(byte_string)                                 #
    raw_samples = [b & 0xFF for b in byte_array]                            # Convert and crop the raw hex data
    raw_samples[:] = raw_samples[2:20]                                      # 

    Sample1 = [0, 0]                                                        #              
    Sample2 = [0, 0]                                                        # Initialize the sample arrays
    Sample3 = [0, 0]                                                        # 

    skip_bit = 0                                                            # This is not a very pythonic way of doing this, but i dont care (:
    i = 0                                                                   #

    while skip_bit < 18:
        for j in range(2):                                                  # Do for each channel
            temp_sample = [0, 0, 0]                                         # Initialize the temporary sample byte array
            for loop in range(3):
                temp_sample[loop] = raw_samples[skip_bit + loop + j * 3]    # Fill the temporary sample byte array with the correct values
            
            val1 = temp_sample[0] & 0xFF                                    # 
            val2 = temp_sample[1] & 0xFF                                    # Convert the bytes to signed integers
            val3 = temp_sample[2] & 0xFF                                    # 
            sample_value = (val1 << 16) + (val2 << 8) + val3                # Combine the bytes to a single value (last 16 bytes are timestamp and are not used)
                                                                            # the binary representation of sample_value would look like this: TTTTTTTTTTTTTTTTssssssss
            if i == 0:
                Sample1[j] = sample_value #& 0xFF                           #  
            elif i == 1:                                                    #
                Sample2[j] = sample_value #& 0xFF                           # Fill the sample arrays with the correct values
            elif i == 2:                                                    #
                Sample3[j] = sample_value #& 0xFF                           #

        skip_bit += 6
        i += 1

    final_sample1 = multiplier(Sample1)                                     #
    final_sample2 = multiplier(Sample2)                                     # Multiply the samples (and turn them into floats)
    final_sample3 = multiplier(Sample3)                                     #

    return final_sample1, final_sample2, final_sample3

def multiplier(sample):
    """
    The function takes a sample array and multiplies each element by 0.4 divided by the maximum possible
    value of a 24-bit sample.
    
    :param sample: The parameter "sample" is a list of numbers that represents a set of samples. Each
    sample is multiplied by a constant value (0.4 / ((2 ** 23) - 1)) and the result is stored in a new
    list called "final_sample". The function then returns the final
    :return: The function `multiplier` returns the final sample array, which is the input sample array
    multiplied by a scaling factor.
    """
    final_sample = [0] * len(sample)                                        # Initialize the final sample array

    for i in range(len(sample)):                                            # Multiply each sample by the correct value
        final_sample[i] = sample[i] * 0.4 / ((2 ** 23) - 1) #* 1000000.0    # very funky stuff going on here, i removed the 1e6 because i dont like it.
    return final_sample                                                         

