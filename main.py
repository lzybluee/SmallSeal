import random
import sys

import matplotlib.pyplot as plt
import pyperclip
from PIL import Image, ImageDraw, ImageFont


class FontShow:
    def __init__(self):
        self.seal = True
        self.index = 0

        self.gbk_seal_font = ImageFont.truetype('FZXZTK.TTF', 460)
        self.seal_font = ImageFont.truetype('FZXZTFW.TTF', 460)
        self.normal_font = ImageFont.truetype('FZXKTK.TTF', 400)

        with open(sys.argv[1] if len(sys.argv) > 1 else 'simp.txt', 'r', encoding='utf8') as file:
            self.chars = list(file.read())
            random.shuffle(self.chars)

        self.fig, ax = plt.subplots(figsize=(200, 200), dpi=1)
        self.im = ax.imshow(self.get_img())

        self.fig.canvas.manager.set_window_title('Seal')
        self.fig.canvas.manager.toolbar.pack_forget()
        self.fig.canvas.mpl_connect('key_press_event', self.on_key)
        self.fig.canvas.mpl_connect('button_press_event', self.on_mouse)
        self.fig.canvas.mpl_connect('scroll_event', self.on_scroll)

        ax.axis('off')

        plt.show()

    def get_img(self):
        img_size = (500, 500)
        image = Image.new('RGB', img_size, color='white')
        draw = ImageDraw.Draw(image)

        font = self.gbk_seal_font if self.seal else self.normal_font
        bbox = draw.textbbox((0, 0), self.chars[self.index], font=font)
        if self.seal and bbox[3] == bbox[1]:
            font = self.seal_font
            bbox = draw.textbbox((0, 0), self.chars[self.index], font=font)

        text_width = bbox[2] - bbox[0]
        text_height = bbox[3] - bbox[1]

        position = ((img_size[0] - text_width) // 2, (img_size[1] - text_height) // 2 - 40)
        draw.text(position, self.chars[self.index], font=font, fill='black')
        return image

    def slide(self, left):
        if left:
            self.index -= 1
            if self.index == -1:
                self.index = len(self.chars) - 1
            self.seal = True
        else:
            self.index += 1
            if self.index == len(self.chars):
                self.index = 0
            self.seal = True

    def get_clip(self):
        if text := pyperclip.paste():
            i = 1
            for c in text:
                if ord(c) > 127:
                    self.chars.insert(self.index + i, c)
                    i += 1
            if i > 1:
                self.index += 1
                self.seal = True

    def on_key(self, event):
        if event.key == 'left':
            self.slide(True)
        elif event.key == 'right':
            self.slide(False)
        elif event.key == 'up' or event.key == 'down':
            self.seal = not self.seal
        elif event.key == 'c' or event.key == 'ctrl+c':
            pyperclip.copy(self.chars[self.index])
        elif event.key == 'v' or event.key == 'ctrl+v':
            self.get_clip()
        else:
            return

        self.show()

    def on_mouse(self, event):
        if event.button == 1:
            self.seal = not self.seal
        elif event.button == 2:
            pyperclip.copy(self.chars[self.index])
        elif event.button == 3:
            self.get_clip()

        self.show()

    def on_scroll(self, event):
        if event.step > 0:
            self.slide(True)
        else:
            self.slide(False)

        self.show()

    def show(self):
        self.im.set_data(self.get_img())
        self.fig.canvas.draw_idle()


if __name__ == '__main__':
    FontShow()
