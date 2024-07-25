import numpy as np
import os
import matplotlib.pyplot as plt
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.model_selection import train_test_split
from imblearn.over_sampling import SMOTE

# Directory containing the NumPy files
data_dir = 'test_data'

# Function to load data
def load_data(data_dir):
    data_list = []
    label_list = []
    for filename in os.listdir(data_dir):
        if filename.endswith('-epoch-data.npy'):
            epoch_data = np.load(os.path.join(data_dir, filename))
            data_list.append(epoch_data)
        elif filename.endswith('-epoch-labels.npy'):
            epoch_labels = np.load(os.path.join(data_dir, filename))
            label_list.extend(epoch_labels)
    return data_list, label_list

# Load the data
data_list, label_list = load_data(data_dir)

# Convert to numpy arrays
if data_list:
    X = np.concatenate(data_list, axis=0)
    y = np.array(label_list)

    # Encode labels
    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)

    # Check class distribution before oversampling
    class_counts = np.bincount(y_encoded)
    class_labels = label_encoder.inverse_transform(np.arange(len(class_counts)))
    print("Class distribution before oversampling:")
    for label, count in zip(class_labels, class_counts):
        print(f"{label}: {count}")

    # Normalize the data
    scaler = StandardScaler()
    X = scaler.fit_transform(X)

    # Oversample the data using SMOTE
    smote = SMOTE(random_state=42)
    X_resampled, y_resampled = smote.fit_resample(X, y_encoded)

    # Check class distribution after oversampling
    class_counts_resampled = np.bincount(y_resampled)
    print("Class distribution after oversampling:")
    for label, count in zip(class_labels, class_counts_resampled):
        print(f"{label}: {count}")

    # Split into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X_resampled, y_resampled, test_size=0.2, random_state=42, stratify=y_resampled)

    print("Data preprocessing complete.")

    # Save preprocessed data
    np.save(os.path.join(data_dir, 'X_train.npy'), X_train)
    np.save(os.path.join(data_dir, 'X_test.npy'), X_test)
    np.save(os.path.join(data_dir, 'y_train.npy'), y_train)
    np.save(os.path.join(data_dir, 'y_test.npy'), y_test)
else:
    print("No valid data files found. Please check your data directory and files.")
