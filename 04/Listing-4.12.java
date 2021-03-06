public void stopDownload() {
  thisThread.interrupt();
}

public void performDownload() {
  int byteCount;
  Runnable progressUpdate = new Runnable() {
    public void run() {
      progressBar.setValue(bytesRead);
      completeLabel.setText(
          Integer.toString(
          bytesRead));
    }
  };
  while ((bytesRead < fileSize) && (!isStopped())) {
    try {
      if (isSleepScheduled()) {
        try {
          Thread.sleep(SLEEP_TIME);
          setSleepScheduled(false);
        }
        catch (InterruptedException ie) {
         setStopped(true);
         break;
        }
      }
      byteCount = inputStream.read(buffer);
      if (byteCount == -1) {
        setStopped(true);
        break;
      }
      else {
        outputStream.write(buffer, 0,
            byteCount);
        bytesRead += byteCount;
        SwingUtilities.invokeLater(
            progressUpdate);
      }
    } catch (IOException ioe) {
      setStopped(true);
      JOptionPane.showMessageDialog(this,
          ioe.getMessage(),
          "I/O Error",
          JOptionPane.ERROR_MESSAGE);
      break;
    }
    synchronized (this) {
      if (isSuspended()) {
        try {
          this.wait();
          setSuspended(false);
        }
        catch (InterruptedException ie) {
          setStopped(true);
          break;
        }
      }
    }
    if (Thread.interrupted()) {
      setStopped(true);
      break;
    }
   }
   try {
     outputStream.close();
     inputStream.close();
   } catch (IOException ioe) {};
  }

}
