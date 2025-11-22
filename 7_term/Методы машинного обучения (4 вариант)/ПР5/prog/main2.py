# 2NN-margin
def calculate_2nn_margin(X, y, object_index):
    target_x = X[object_index]
    target_y = y[object_index]
    distances = []
    for i, (x, label) in enumerate(zip(X, y)):
        if i != object_index:
            dist = abs(x - target_x)
            distances.append((dist, label, i))
    distances.sort(key=lambda x: x[0])
    neighbor1_dist, neighbor1_label, neighbor1_idx = distances[0]
    neighbor2_dist, neighbor2_label, neighbor2_idx = distances[1]
    print(f"{object_index} ({target_x:.2f}, {target_y}):")
    print(f"  1NN: {neighbor1_idx}, {neighbor1_dist:.2f}, {neighbor1_label}")
    print(f"  2NN: {neighbor2_idx}, {neighbor2_dist:.2f}, {neighbor2_label}")
    same_class_distances = [dist for dist, label, idx in distances[:2] if label == target_y]
    diff_class_distances = [dist for dist, label, idx in distances[:2] if label != target_y]
    if same_class_distances and diff_class_distances:
        margin = min(diff_class_distances) - min(same_class_distances)
    elif same_class_distances:
        margin = float('inf')
    else:
        margin = -float('inf')
    print(f"  2NN margin: {margin:.2f}")
    if margin > 0:
        print("  object OK")
    elif margin < 0:
        print("  object ERROR")
    else:
        print("  object on margin")
    return margin

selected_object = 4
margin2nn = calculate_2nn_margin(X, y, selected_object)

print("=" * 50)
print("Margins for all objects:")
print("=" * 50)
margins = []
for i in range(len(X)):
    margin = calculate_2nn_margin(X, y, i)
    margins.append(margin)
