const MAX_DIMENSION = 256;
const JPEG_QUALITY = 0.82;

/**
 * Downscales an image file to a square (center-cropped), JPEG-compressed data URI suitable for
 * storing inline in the database as a profile picture — no object storage is configured for this
 * deployment, so uploads are stored directly on the user row rather than in a bucket.
 */
export function resizeImageToDataUri(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = () => reject(new Error("Failed to read file."));
    reader.onload = () => {
      const img = new Image();
      img.onerror = () => reject(new Error("File is not a valid image."));
      img.onload = () => {
        const size = Math.min(img.width, img.height);
        const sx = (img.width - size) / 2;
        const sy = (img.height - size) / 2;
        const outputSize = Math.min(MAX_DIMENSION, size);

        const canvas = document.createElement("canvas");
        canvas.width = outputSize;
        canvas.height = outputSize;
        const ctx = canvas.getContext("2d");
        if (!ctx) {
          reject(new Error("Canvas is not supported in this browser."));
          return;
        }
        ctx.drawImage(img, sx, sy, size, size, 0, 0, outputSize, outputSize);
        resolve(canvas.toDataURL("image/jpeg", JPEG_QUALITY));
      };
      img.src = reader.result as string;
    };
    reader.readAsDataURL(file);
  });
}
