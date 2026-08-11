
import json
import random

from .hardcoded import SPECIALTIES
from faker import Faker


def gen_matricule():
    return str(random.randint(100_000_000_000, 999_999_999_999))


def gen_phoneNumber():
    return "0" + random.choice(["5", "6", "7"]) + str(random.randint(10_000_000, 99_999_999))


fake = Faker('fr_DZ')

specialties = []
collection = {
    "specialties": specialties
}

for specialty_data in SPECIALTIES:

    academicLevels = []
    specialty = {
        'name': specialty_data['name'],
        'acronyme': specialty_data['acronyme'],
        'cycle': specialty_data['cycle'],
        'academicLevels': academicLevels,
    }
    specialties.append(specialty)

    for academicLevel_number, academicLevel_data in enumerate(specialty_data['academicLevels']):
        sections = []
        academicLevel = {
            'number': academicLevel_number + 1,
            'sections': sections,
        }
        academicLevels.append(academicLevel)

        for section_number in range(1, academicLevel_data['nb_sections'] + 1):

            groups = []
            section = {
                'number': section_number,
                'groups': groups,
            }
            sections.append(section)

            for group_number in range(1, academicLevel_data['nb_groups_per_section'] + 1):

                [surname, name] = fake.name().split(" ")
                students = []
                group = {
                    'number': group_number,
                    'teachingAssistant': {
                        'name': name,
                        'surname': surname,
                        'email': name + "." + surname + "@gmail.com",
                        'phoneNumber': gen_phoneNumber(),
                    },
                    'students': students,
                }
                groups.append(group)

                for student_number in range(1, academicLevel_data['nb_students_per_group'] + 1):
                    [surname, name] = fake.name().split(" ")
                    student = {
                        'name': name,
                        'surname': surname,
                        'email': name + "." + surname + "@gmail.com",
                        'matricule': gen_matricule(),
                    }
                    students.append(student)


with open('src/test/resources/samples/pedagogical_structure.json', 'w') as f:
    json.dump(collection, f, indent=2)
    pass
