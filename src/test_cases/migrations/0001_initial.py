# -*- coding: utf-8 -*-
# Generated by Django 1.11.2 on 2017-06-09 15:20
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Action',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('description', models.TextField(help_text='A description of this Action', verbose_name='Description')),
                ('action', models.CharField(choices=[['Click', 'Click'], ['EnterText', 'Enter Text'], ['SelectFromDropDown', 'Select From Drop Down'], ['SelectFromLookUp', 'Select From Look Up'], ['URL', 'URL']], max_length=255)),
                ('locators', models.CharField(choices=[['ID', 'ID'], ['NAME', 'Name'], ['XPATH', 'X Path'], ['CSSSELECTOR', 'CSS Selector'], ['LINKTEXT', 'Link Text'], ['CLASSNAME', 'Class Name'], ['TAGNAME', 'Tag Name'], ['PARTIALLINKTEXT', 'Partial Link Text']], max_length=255)),
                ('element_identifier', models.CharField(max_length=1024)),
                ('element_value', models.CharField(blank=True, max_length=1024)),
            ],
        ),
        migrations.CreateModel(
            name='Project',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255, verbose_name='Project Name')),
            ],
        ),
        migrations.CreateModel(
            name='UseCase',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('use_case_name', models.CharField(max_length=255, verbose_name='Use Case Name')),
                ('use_case_description', models.TextField(blank=True, help_text='A short description of your Use Case', verbose_name='Use Case Description')),
                ('project', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='test_cases.Project')),
            ],
        ),
        migrations.AddField(
            model_name='action',
            name='use_case',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='test_cases.UseCase'),
        ),
    ]