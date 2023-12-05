import mne
from scipy.ndimage import gaussian_filter1d
import numpy as np
import matplotlib.pyplot as plt
import time

from mne_lsl.datasets import sample
from mne_lsl.lsl import local_clock
from mne_lsl.player import PlayerLSL as Player
from mne_lsl.stream import StreamLSL as Stream

tentwenty = mne.channels.make_standard_montage("standard_1020")
tentwenty.plot()

# create the info structure needed by mne
info = mne.create_info(ch_names=['Fp1', 'Fp2'], sfreq=250, ch_types=['eeg', 'eeg'],)




