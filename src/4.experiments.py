import numpy as np
import os
import random
import matplotlib.pyplot as plt
from sklearn.preprocessing import LabelEncoder
from tensorflow.keras.models import load_model
import matplotlib
matplotlib.use('TkAgg')

# Directory containing the preprocessed data
data_dir = 'test_data'

# Load the trained model
model = load_model('eeg_classification_model.h5')

# Load preprocessed data
X_test = np.load(os.path.join(data_dir, 'X_test.npy'))
y_test = np.load(os.path.join(data_dir, 'y_test.npy'))

# Encode labels
label_encoder = LabelEncoder()
y_test_encoded = label_encoder.fit_transform(y_test)

# Make predictions
y_pred_probs = model.predict(X_test)
y_pred = np.argmax(y_pred_probs, axis=1)

# Decode labels back to original
y_test_labels = label_encoder.inverse_transform(y_test_encoded)
y_pred_labels = label_encoder.inverse_transform(y_pred)

# Select a random subset of the data for visualization
num_samples = min(50, len(y_test_labels))
indices = random.sample(range(len(y_test_labels)), num_samples)
y_test_labels_subset = y_test_labels[indices]
y_pred_labels_subset = y_pred_labels[indices]

# Plotting
plt.figure(figsize=(12, 6))
for i, (actual, predicted) in enumerate(zip(y_test_labels_subset, y_pred_labels_subset)):
    color = 'green' if actual == predicted else 'red'
    plt.scatter(i, 1, color=color, s=100)  # Plot a dot at position i
    plt.text(i, 1.05, actual, horizontalalignment='center', verticalalignment='center', fontsize=10, color='black')
    plt.text(i, 0.95, predicted, horizontalalignment='center', verticalalignment='center', fontsize=10, color='black')

plt.xlim(-1, num_samples)
plt.ylim(0.5, 1.5)
plt.title('Actual vs Predicted Movements')
plt.axis('off')
plt.show()
