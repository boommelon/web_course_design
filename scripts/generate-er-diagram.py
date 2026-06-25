from pathlib import Path
from math import atan2, cos, sin

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs" / "figures" / "er-diagram.png"
FONT_DIR = Path("C:/Windows/Fonts")

W, H = 1800, 1220
SCALE = 2
INK = "#111111"
LIGHT = "#f6f6f6"


def s(value: float) -> int:
    return int(round(value * SCALE))


def xy(values):
    return tuple(s(v) for v in values)


def load_font(size: int, bold: bool = False):
    candidates = ["msyhbd.ttc", "simhei.ttf", "msyh.ttc"] if bold else ["msyh.ttc", "simsun.ttc", "simhei.ttf"]
    for name in candidates:
        path = FONT_DIR / name
        if path.exists():
            return ImageFont.truetype(str(path), size * SCALE)
    return ImageFont.load_default()


img = Image.new("RGB", (W * SCALE, H * SCALE), "white")
draw = ImageDraw.Draw(img)

FONT_TITLE = load_font(32, True)
FONT_NODE = load_font(20, False)
FONT_NODE_BOLD = load_font(21, True)
FONT_SMALL = load_font(17, False)


def text_box(text: str, font):
    box = draw.textbbox((0, 0), text, font=font)
    return box[2] - box[0], box[3] - box[1]


def center_text(cx: float, cy: float, text: str, font, underline: bool = False):
    tw, th = text_box(text, font)
    x = s(cx) - tw / 2
    y = s(cy) - th / 2 - s(1)
    draw.text((x, y), text, fill=INK, font=font)
    if underline:
        yy = y + th + s(3)
        draw.line((x, yy, x + tw, yy), fill=INK, width=s(1.5))


def line(p1, p2, width=2):
    draw.line((s(p1[0]), s(p1[1]), s(p2[0]), s(p2[1])), fill=INK, width=s(width))


def ellipse_edge(cx, cy, rx, ry, target):
    angle = atan2(target[1] - cy, target[0] - cx)
    return cx + rx * cos(angle), cy + ry * sin(angle)


def rect_edge(cx, cy, w, h, target):
    dx = target[0] - cx
    dy = target[1] - cy
    if dx == 0 and dy == 0:
        return cx, cy
    sx = (w / 2) / abs(dx) if dx else float("inf")
    sy = (h / 2) / abs(dy) if dy else float("inf")
    scale = min(sx, sy)
    return cx + dx * scale, cy + dy * scale


def diamond_edge(cx, cy, w, h, target):
    dx = target[0] - cx
    dy = target[1] - cy
    denom = abs(dx) / (w / 2) + abs(dy) / (h / 2)
    if denom == 0:
        return cx, cy
    return cx + dx / denom, cy + dy / denom


entities = {
    "topic": (310, 295, 145, 58, "课题"),
    "user": (845, 285, 145, 58, "用户"),
    "doc": (1350, 295, 145, 58, "文档"),
    "score": (1300, 695, 145, 58, "成绩"),
    "config": (300, 735, 170, 58, "系统配置"),
    "notice": (810, 940, 145, 58, "公告"),
}

relations = {
    "publish": (585, 295, 100, 72, "发布"),
    "select": (575, 485, 110, 78, "选择"),
    "upload": (1105, 295, 100, 72, "上传"),
    "grade": (1055, 565, 100, 72, "评分"),
    "show": (835, 720, 128, 76, "按角色展示"),
    "control": (430, 610, 116, 74, "控制流程"),
}

attrs = [
    ("topic", 210, 110, 110, 42, "课题ID", True),
    ("topic", 95, 260, 128, 42, "课题标题", False),
    ("topic", 125, 420, 138, 42, "课题描述", False),
    ("topic", 330, 485, 128, 42, "课题状态", False),
    ("topic", 465, 165, 128, 42, "审核状态", False),
    ("topic", 500, 390, 128, 42, "人数上限", False),
    ("user", 845, 100, 112, 42, "用户ID", True),
    ("user", 690, 165, 110, 42, "用户名", False),
    ("user", 650, 420, 96, 42, "密码", False),
    ("user", 845, 435, 96, 42, "姓名", False),
    ("user", 995, 405, 96, 42, "角色", False),
    ("user", 1035, 165, 96, 42, "电话", False),
    ("user", 990, 95, 96, 42, "邮箱", False),
    ("doc", 1300, 105, 112, 42, "文档ID", True),
    ("doc", 1495, 165, 116, 42, "文件名", False),
    ("doc", 1530, 390, 130, 42, "文件路径", False),
    ("doc", 1340, 455, 130, 42, "文件类型", False),
    ("doc", 1170, 405, 130, 42, "上传时间", False),
    ("doc", 1165, 170, 130, 42, "审核状态", False),
    ("score", 1300, 570, 112, 42, "成绩ID", True),
    ("score", 1110, 780, 112, 42, "综合分", False),
    ("score", 1285, 860, 112, 42, "答辩分", False),
    ("score", 1470, 770, 128, 42, "答辩评语", False),
    ("score", 1495, 640, 128, 42, "互评意见", False),
    ("score", 1140, 880, 128, 42, "自评意见", False),
    ("config", 180, 620, 112, 42, "配置键", True),
    ("config", 100, 760, 112, 42, "配置名", False),
    ("config", 315, 885, 112, 42, "配置值", False),
    ("config", 510, 790, 128, 42, "更新时间", False),
    ("select", 420, 515, 128, 42, "申请理由", False),
    ("select", 565, 625, 128, 42, "选题状态", False),
    ("select", 725, 515, 128, 42, "选题轮次", False),
    ("notice", 650, 820, 112, 42, "公告ID", True),
    ("notice", 820, 795, 128, 42, "公告标题", False),
    ("notice", 1005, 830, 128, 42, "公告内容", False),
    ("notice", 1020, 950, 128, 42, "是否置顶", False),
    ("notice", 665, 1080, 128, 42, "发布时间", False),
]


def entity_center(name):
    return entities[name][0], entities[name][1]


def relation_center(name):
    return relations[name][0], relations[name][1]


def entity_point(name, target):
    cx, cy, w, h, _ = entities[name]
    return rect_edge(cx, cy, w, h, target)


def relation_point(name, target):
    cx, cy, w, h, _ = relations[name]
    return diamond_edge(cx, cy, w, h, target)


def attr_point(attr, target):
    _, cx, cy, w, h, _, _ = attr
    return ellipse_edge(cx, cy, w / 2, h / 2, target)


def link_entity_relation(entity, relation):
    ep = entity_point(entity, relation_center(relation))
    rp = relation_point(relation, entity_center(entity))
    line(ep, rp)


def link_relation_relation(a, b):
    ap = relation_point(a, relation_center(b))
    bp = relation_point(b, relation_center(a))
    line(ap, bp)


def label(text, x, y):
    draw.text((s(x), s(y)), text, fill=INK, font=FONT_SMALL)


def draw_entity(name):
    cx, cy, w, h, text = entities[name]
    draw.rectangle(xy((cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2)), outline=INK, width=s(2))
    center_text(cx, cy, text, FONT_NODE_BOLD)


def draw_relation(name):
    cx, cy, w, h, text = relations[name]
    points = [(cx, cy - h / 2), (cx + w / 2, cy), (cx, cy + h / 2), (cx - w / 2, cy)]
    draw.polygon([xy(p) for p in points], outline=INK, fill="white")
    draw.line([xy(points[0]), xy(points[1]), xy(points[2]), xy(points[3]), xy(points[0])], fill=INK, width=s(2))
    center_text(cx, cy, text, FONT_SMALL if len(text) > 3 else FONT_NODE)


def draw_attr(attr):
    owner, cx, cy, w, h, text, pk = attr
    draw.ellipse(xy((cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2)), outline=INK, width=s(1.6))
    center_text(cx, cy, text, FONT_SMALL, underline=pk)


def owner_center(owner):
    if owner in entities:
        return entity_center(owner)
    return relation_center(owner)


def owner_anchor(owner, target):
    if owner in entities:
        return entity_point(owner, target)
    return relation_point(owner, target)


# Title
center_text(W / 2, 46, "毕业设计管理系统  ER  图", FONT_TITLE)

# Relationship links
link_entity_relation("topic", "publish")
link_entity_relation("user", "publish")
label("N", 445, 280)
label("1", 725, 280)

link_entity_relation("topic", "select")
link_entity_relation("user", "select")
label("N", 442, 410)
label("1", 740, 425)

link_entity_relation("user", "upload")
link_entity_relation("doc", "upload")
label("1", 965, 280)
label("N", 1240, 280)

link_entity_relation("user", "grade")
link_entity_relation("score", "grade")
label("1", 940, 435)
label("N", 1170, 575)

link_entity_relation("user", "show")
link_entity_relation("notice", "show")
label("N", 865, 485)
label("1", 815, 835)

link_entity_relation("config", "control")
link_relation_relation("control", "select")

# Attribute links
for attr in attrs:
    target = owner_center(attr[0])
    line(owner_anchor(attr[0], (attr[1], attr[2])), attr_point(attr, target), width=1.3)

# Draw nodes over the lines.
for attr in attrs:
    draw_attr(attr)

for name in relations:
    draw_relation(name)

for name in entities:
    draw_entity(name)

draw.text(
    (s(330), s(1165)),
    "说明：矩形表示实体，椭圆表示属性，带下划线的属性为主键，菱形表示实体之间的联系，1/N 表示联系基数。",
    fill=INK,
    font=FONT_SMALL,
)

OUT.parent.mkdir(parents=True, exist_ok=True)
img = img.resize((W, H), Image.Resampling.LANCZOS)
img.save(OUT)
print(f"Generated {OUT}")
