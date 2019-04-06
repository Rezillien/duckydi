import numpy as np
import sys
import cv2
cv2.namedWindow("preview")
cap = cv2.VideoCapture(0)

if cap.isOpened(): # try to get the first frame
    rval, frame = cap.read()
else:
    rval = False


# take first frame of the video
ret,frame = cap.read()

# setup initial location of window
r,h,c,w = 250,90,400,125  # simply hardcoded the values
r2,h2,c2,w2 = 250,90,100,125  # simply hardcoded the values
track_window = (c,r,w,h)
track_window2 = (c2,r2,w2,h2)

# set up the ROI for tracking
roi = frame[r:r+h, c:c+w]
roi2 = frame[r2:r2+h2, c2:c2+w2]
hsv_roi =  cv2.cvtColor(roi, cv2.COLOR_BGR2HSV)
hsv_roi2 =  cv2.cvtColor(roi2, cv2.COLOR_BGR2HSV)
mask = cv2.inRange(hsv_roi, np.array((0., 60.,32.)), np.array((180.,255.,255.)))
mask2 = cv2.inRange(hsv_roi2, np.array((0., 60.,32.)), np.array((180.,255.,255.)))
roi_hist = cv2.calcHist([hsv_roi],[0],mask,[180],[0,180])
roi_hist2 = cv2.calcHist([hsv_roi2],[0],mask2,[180],[0,180])
cv2.normalize(roi_hist,roi_hist,0,255,cv2.NORM_MINMAX)
cv2.normalize(roi_hist2,roi_hist2,0,255,cv2.NORM_MINMAX)

# Setup the termination criteria, either 10 iteration or move by atleast 1 pt
term_crit = ( cv2.TERM_CRITERIA_EPS | cv2.TERM_CRITERIA_COUNT, 10, 1 )
lower_range = np.array([20, 100, 100], dtype=np.uint8)
upper_range = np.array([30, 255, 255], dtype=np.uint8)



while(1):
    ret ,frame = cap.read()

    if ret == True:
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        fgmask = cv2.inRange(hsv, lower_range, upper_range)
        cv2.imshow('fgmask',fgmask)
        cv2.bitwise_not(fgmask,fgmask)
        dst = cv2.calcBackProject([fgmask],[0],roi_hist,[0,180],1)
        dst2 = cv2.calcBackProject([fgmask],[0],roi_hist2,[0,180],1)

        # apply meanshift to get the new location
        ret, track_window = cv2.meanShift(dst, track_window, term_crit)
        ret2, track_window2 = cv2.meanShift(dst2, track_window2, term_crit)

        # Draw it on image
        x,y,w,h = track_window
        x2,y2,w2,h2 = track_window2
        img2 = cv2.rectangle(frame, (x,y), (x+w,y+h), 255,2)
        img3 = cv2.rectangle(frame, (x2,y2), (x2+w2,y2+h2), 255,2)
        cv2.imshow('img3',img3)

        print(x,";",y,";",x2,";",y2)
        sys.stdout.flush()

        k = cv2.waitKey(60) & 0xff
        if k == 27:
            break
        else:
            cv2.imwrite(chr(k)+".jpg",img2)

    else:
        break

cv2.destroyAllWindows()
cap.release()
