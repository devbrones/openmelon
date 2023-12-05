#import mne
from scipy.ndimage import gaussian_filter1d
import numpy as np
import matplotlib.pyplot as plt
import time

from mne_lsl.datasets import sample
from mne_lsl.lsl import local_clock
from mne_lsl.player import PlayerLSL as Player
from mne_lsl.stream import StreamLSL as Stream

"""
Read the raw data from a lsl stream

Format: [Fp1, Fp2]

Then do a continuos transform splitting the data into the 5 frequency bands (delta, theta, alpha, beta, gamma)
then plot the data in real time

done using mne-lsl
"""

# frequency ranges for the power bands
freq_ranges = {"delta": [0.5, 4], "theta": [4, 8], "alpha": [8, 12], "beta": [12, 30], "gamma": [30, 100]}

# Connect to the LSL stream
stream_name = 'MelonEEG_Stream'  # Stream name from the sender script
raw =
